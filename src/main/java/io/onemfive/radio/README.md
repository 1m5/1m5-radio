# io.onemfive.radio package
The radio package consists of the following classes and packages.

## Classes
* [RadioSensor](RadioSensor.java) - this class is the entry point into the whole package.
It is instantiated within the SensorsService when configured so. On startup, this object
loads the configured radio defined in radio-sensor.config, instantiates it, then starts
up the particular Radio. The current default defined in Maven is [GNURadio](vendor/gnuradio/GNURadio.java)
found in the vendor package. Once the radio has started, a [RadioSession](RadioSession.java)
is instantiated and configured for each entry in the signals.json file found on the classpath.


## Packages
* bands
* channels
* contacts
* detection
* discovery
* filters
* hardware
* jamming
* satellite
* signals
* spread
* tasks
* tuning
* vendor