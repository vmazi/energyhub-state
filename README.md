# energyhub-state

Hello everyone,

This is my attempted solution to the energy hub coding assignment

https://gist.github.com/jdangerx/883b9057c5af65e6965acc646f445c3e

Now this project is a maven project in java, so you will need java 1.8 and either maven installed in your system or a plugin for your 
ide in order to build it.

in order to build it go into the root directory, (which this readme should be in).
the pom.xml should also be visible in it as well.
then simply run
mvn clean package

this will build two jars in a newly created target folder,
state.0.0.1.jar
and 
state.0.0.1-jar-with-dependencies.jar

in order to execute it, I suggest using the standalone jar with dependencies bundled in

basic operation goes as follows

java -jar state-0.0.1-jar-with-dependencies.jar [--field <field>[...]] <uri> <date-time>

so for example, lets do an s3 call 

java -jar state-0.0.1-jar-with-dependencies.jar  --field ambientTemp --field schedule s3://net.energyhub.assets/public/dev-exercises/audit-data/ 2016-01-01T03:00

Example of a file system call (on windows with windows powershell)
*Note the folders must be in taken out of the tarball (BUT files must be still in .jsonl.gz format)
so that the directory structure looks something like
C:\Users\vamsi\Documents\EnergyHubData
        - 2016
           - 01
              - 01.jsonl.gz
              - 02.jsonl.jz
                  .....
                  .....
           - 02
            .....
            .....
            
 java -jar state-0.0.1-jar-with-dependencies.jar  --field ambientTemp --field schedule C:\Users\vamsi\Documents\EnergyHubData  2016-01-01T03:00
 
 
 Example of a file system call (on unix as well as on windows with a bash shell) 
 
 java -jar state-0.0.1-jar-with-dependencies.jar  --field ambientTemp --field schedule C:/Users/vamsi/Documents/EnergyHubData 2016-01-01T03:00
 
 (basically call it how youd expect on a unix machine, 
 however if calling via bash shell on windows you need to provide the directory unix-style, not windows style)
 
 
