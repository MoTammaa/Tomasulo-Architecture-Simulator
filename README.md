# Tomasulo-Architecture-Simulator
Simulation of Tomasulo Architecture

**Introduction**

This report aims to outline our development process for a program that simulates Tomasulo’s algorithm on a MIPS processor.

The simulator runs Tomasulo’s algorithm in its most basic form without making use of any extensions to the algorithm, such as a reorder buffer or other instruction level parallelism techniques such as branch prediction.

Instructions are written line by line using the format outlined in the examples below in a text file. The text file is then loaded by the program, and the instructions therein are executed. You can also include comments in text files (a comment is a line starting with //, much like comments in C-style programming languages) and they will not be read by the program.

Address clashes are ignored. When two instructions finish executing and need to write to the bus in the same cycle, a FIFO (first in first out) approach is used. The instruction that started executing first writes back first.

**Supported Instructions and Formats**

  **Format**

    **Integer and Floating Point Instructions**

      **INSTR Rs, Rt, Rd** 
      Example: ADD R0, R1, R3

     Control Instructions
       INSTR Rs, LABEL
       Example: BNEZ R3, LOOP

     Labels are written before instructions. For example, to add the label to the
     instruction ADD R0, R1, R3, the instruction would be written in the text file as:
     LOOP: ADD R0, R1, R3

The simulator supports the following instructions:

**Integer and Floating Point Instructions**

 1. Addition (ADD)
 e.g: ADD R0, R1, R8 or ADD F0, F4, F6
 2. Subtraction (SUB)
 e.g: SUB R3, R2, R5 or SUB F12, F9, F22
 3. Add Immediate (ADDI)
 e.g: ADDI R1, R2, 100 (R1 = R2 + 100) or ADDI F3, F5, F19
 4. Subtract Immediate (SUBI)
 e.g: SUBI F0, F9, 145
 5. Multiplication (MUL)
 e.g: MUL F3, F2, F15
 6. Multiply Immediate (MULI)
 e.g: MULI R3, R2, R27
 7. Division (DIV)
 e.g: DIV F31, F12, F29
 8. Divide Immediate (DIVI)
 e.g: DIVI R18, R20, R7
 9. Load (LOAD)
 e.g: LOAD F1, R2, 150 //F1 ← Content of memory address R2 + 150.
 Content is converted from int to float if the destination register is a floating
 point register.
 10. Store (STORE)
 e.g: R1(100), R0 //R0 ← Content of memory address R1 + 100
 11. Halt (HLT)
 e.g: HLT //Halts the program. All subsequent instructions will not be
 executed.
 
**Control Instructions**

 1. Branch if not equal zero (BNEZ)
 e.g: BNEZ R0, Label1 //Branches to the instruction at Label1 if R0 is not
 equal to zero.

 Note that the instructions above are used for both integer as well as floating point operations. You may notice other instruction types such as L.D and S.D in
 the files. These are just aliases for existing instructions (in this case, for LOAD and
 STORE respectively, and not different instruction types. All instructions supported
 by the program have been covered above.

**Program Structure**
 ```bash
   src
   │
   ├── caches
   │ ├── Cache
   │ ├── DataCache
   │ └── InstructionCache
   │
   ├── engine
   │ └── Tomasulo
   │
   ├── Instruction
   │ ├── Instruction
   │ ├── InstructionStatus
   │ └── ITypes
   │
   ├── registerFile
   │ ├── Register
   │ └── RegisterFile
   │
   ├── reservationStations
   │ ├── LoadStoreBuffer
   │ ├── ReservationStation
   │ └── Station
   │
   ├── View
   │ └── Tables
   │
   └── <Instruction Text Files, i.e ins1.txt>
```

**Program Component Overview**

From the structure above, we can see that the program is divided into five main packages (caches, engine, instruction, registerFile, and reservationStations).

The view package contains the class required to run the GUI but has no bearing on the program’s logic.

**Classes**

- **Cache:** Parent class of other Cache types. Defines attributes and abstract methods to be implemented by child classes.
- **DataCache:** Represents the memory of the program (to store data values).
- **InstructionCache:** Represents the instruction memory of the program. Contains methods to add and issue instructions, set a specific address to a specific instruction, and others.
- **Tomasulo:** This is the driver class and entry point of the program. It also contains the main method that runs the program. The class initializes the Tomasulo algorithm with the specified number of reservation stations and the CPI for each instruction. It also initializes all registers in the register file. It contains all main methods to issue, decode, execute, and write back an instruction.
- **Instruction:** Object representing an instruction. Contains method to parse instruction according to its type and a method to print its contents.
- **InstructionStatus:** An object representing the status of the Instruction (issued, executing, executed, or writing back). Contains methods to set the state of the instruction. This object is used in the Instruction class.
- **ITypes:** Represents all the instruction types. This object is used in the Instruction class.
- **Register:** Object representing a register. Contains methods to set value and print register contents. This object is used in the RegisterFile class.
- **RegisterFile:** Object representing a register file. A register file is a file (array) of Register objects. It contains two files: one for integer registers and another for floating-point registers, both of size 32 by default.
- **Station:** Much like the Cache class in the Caches package, this class is the parent class to all reservation station types. It contains the attributes and abstract methods to be implemented by any reservation station.
- **ReservationStation:** Represents reservation stations for normal instruction (ADD, SUB, MUL, etc.). Contains methods to place an instruction in a station, remove an instruction from a station and write it back, and set the values of fields in a reservation station.
- **LoadStoreBuffer:** Represents load/store buffers. Contains similar methods to ReservationStation.

**Test Cases**

We tried to devise a number of test cases that include all possible hazards (namely RAW, WAR, and WAW) in various combinations, as well as in combination with branches.

You can, of course, devise your own test cases (which we pray will not break our program). The following are (most) of the test cases we've used. Unfortunately, we deleted or overwrote some of the cases we had used.

**Set 1:**
```MIPS
MUL R3, R1, R2
ADD R5, R3, R4
ADD R7, R2, R6
ADD R10, R8, R9
MUL R11, R7, R10
ADD R5, R5, R11
```
**Set 2:**
```MIPS
LOAD R1 ,200
ADD F1, F2, F3
SUB F3, F1, F2
MUL F2, F1, F3
DIV F3, F1, F2
ADD F1, F2, F3
SUB F3, F1, F2
STORE F3, 499
```
**Set 3:**
```MIPS
ADD F1, F1, F11
ONE: SUB F3,F1,F2
MUL F2, F1, F3
TWO: DIV F3, F1, F2
ADD F1, F1, F11
BNEZ F1, ONE
SUB F3, F1, F2
BNEZ F5, TWO
```
**Set 4:**
```MIPS
ADD F2, F4, F6
LOAD F8, 0(R1)
SUB F10, F2, F12
MUL F14, F8, F16
DIV F18, F10, F20
BNEZ R1, Label
ADD F22, F16, F24
Label: SUB F26, F12, F28
ADD R5, R2, R3
STORE R5, 0(R1)
```
