package reservationStations;

import engine.Tomasulo;
import instruction.Instruction;

public abstract class Station {
    String name;
    Instruction instruction;

    public Instruction getInstruction() {
        return instruction;
    }

    public abstract void issueInstruction(Instruction instruction);
    public abstract boolean isOccupied();
    public abstract boolean isReady();
    public abstract void occupy();
    public abstract void release();
    public abstract void writeBack();


    public void broadcastResult() {
        String result = instruction.execute(this);
        if (result == null) return;

        for(ReservationStation[] reservationStations
                : new ReservationStation[][]{Tomasulo.getMulDivReservationStations(), Tomasulo.getAddSubReservationStations()}) {
            for (ReservationStation station : reservationStations) {
                if (!station.isOccupied()) continue;
                if (station.getQj().equals(name)) {
                    station.setQj("0");
                    station.setVj(result);
                }
                if (station.getQk().equals(name)) {
                    station.setQk("0");
                    station.setVk(result);
                }
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
