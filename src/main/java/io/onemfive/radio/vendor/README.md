# Vendor Radios
Vendor integration includes all vendor implementations required together to satisfy all of the requirements below.
 
1. Ability to use the full radio electromagnetic spectrum, from 3Hz to 3THz.
2. Support sending/receiving messages globally with no infrastructure.
3. Be able to use current infrastructure without a SIM card.
4. Be able to use current infrastructure with a SIM card.

## GNU Radio
This implementation is the bare bones foundation upon which most higher-level implementations
are built. It is selected to ensure coverage in the event higher-level implementations can not
provide full coverage. It also may not be enough for completely coverage on its own. Written
in C++ with most mainstream platforms supported.

## JRadio
This project is implementing GNU Radio in Java.

