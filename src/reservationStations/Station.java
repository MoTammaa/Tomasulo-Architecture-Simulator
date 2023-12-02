package reservationStations;

import engine.Tomasulo;
import instruction.Instruction;

public abstract class Station {
    String name;
    Instruction instruction;

    public Instruction getInstruction() {
        return instruction;
    }



    public void broadcastResult() {
        String result = instruction.execute(this);

        for (ReservationStation reservationStation : Tomasulo.getAddSubReservationStations()) {
            if (!reservationStation.isOccupied()) continue;

            if (reservationStation.getQj().equals(name)) {
                reservationStation.setQj("0");
                reservationStation.setVj(result);
            }
            if (reservationStation.getQk().equals(name)) {
                reservationStation.setQk("0");
                reservationStation.setVk(result);
            }
        }

        for (ReservationStation reservationStation : Tomasulo.getMulDivReservationStations()) {
            if (!reservationStation.isOccupied()) continue;

            if (reservationStation.getQj().equals(name)) {
                reservationStation.setQj("0");
                reservationStation.setVj(result);
            }
            if (reservationStation.getQk().equals(name)) {
                reservationStation.setQk("0");
                reservationStation.setVk(result);
            }
        }

        // now for the store buffers
        for (LoadStoreBuffer sBuffer : Tomasulo.getStoreBuffers()) {
            if (!sBuffer.isOccupied()) continue;
            if (sBuffer.getQ().equals(name)) {
                sBuffer.setQ("0");
                sBuffer.setFu(result);
            }
        }
    }
}
