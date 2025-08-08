# Demo: jide_demo 1.4 with GraalVM and Java 21  

## Setup

**Step 1: Install needed software packages**

**On Linux**

Install the following packages via the package manager: `g++ make zlib1g-dev`  
_The exact package name may vary, depending on the distribution you use._

You can use a Windows virtual machine to create Windows executables.  
The VM solution that will also automate downloading a Windows installation ISO
is [quickemu](https://github.com/quickemu-project/quickemu).

**On Windows**

On Windows, install Microsoft Visual Studio 2017
with Microsoft Visual C++ (MSVC) 15 or later.  
The Liberica documentation claims that
you need to run all build commands from the `x64 Native Tools Command Prompt`,
but I found that this would fail with C++ errors.
To my surprise, I found it would build without a hitch
from the normal CMD command prompt.  
Different installation options gave different errors,
so my choices might be relevant: MS Visual Studio 2022,
with just "Desktop Development with C++" added and nothing else changed.

For creating Linux executables, it might be enough to use WSL;
if not, you'll need a virtual machine like Hyper-V or VirtualBox,
and download and install any Linux distribution. 

**Step 2: Install the Liberica NIK 23 (JDK 21)**

_NIK 23 (JDK 21) was the latest NIK for an LTS JDK when this demo was made._

_NIK = Native Image Kit; NIKs are also JDKs._  
_LTS JDK = Long-Term Support Java Development Kit._

1. Open https://bell-sw.com/pages/downloads/native-image-kit/#nik-23-(jdk-21)
   in your browser.
2. Go to the entry for your operating system (Linux, Mac, ...).  
_Note: If you do not know what Alpine Linux is,
you do not use it and want another entry._
3. Select the processor architecture (x86, Arm, ...) you wish to build on and for.  
   _Unfortunately, GraalVM cannot build native images __for__ any OS/architecture
   other than the one it is running __on__, i.e. it cannot cross-compile.  
   The workarounds for this situation are for another demo._
4. Use the TAR.GZ download link (ZIP for Windows).
5. Unpack the downloaded file into one of your directories.
   _My standard IDE stores downloaded JDKs in `~/.jdks`, so I use that._  
   This will create a folder named `bellsoft-liberica-vm-core-openjdk21-23.1.8`
   or similar.  
   Copy the full, absolute folder name.
6. In `jh` (`jh.bat` for Windows),
   replace the text after JAVA_HOME= with the folder name from step 5.

If you use IntelliJ, you can register the NIK as a JDK:

1. In the main menu, select: File -> Project Structure.
2. In the left pane, select: Platform Settings -> SDKs.
3. Click the Plus sign at the top of the window and select: Add JDK from Disk.
4. Select the `bellsoft-liberica-vm-core-openjdk21-23.1.8` folder
   created when unpacking the archive.
5. The JDK will register as graalvm-21.
   This is a bit unspecific since there are other graalvm packages,
   so feel free to select a better name.  
   _My personal JDK naming convention is vendor-javaVersion;
   since the NIK has a NIK and a JDK version,
   I use liberica-nik-23-java-21._
6. In the left pane, now select Project Settings -> Project.
7. In the SDK field, select liberica-nik-23-java-21,
   or whatever name you used in step 5.


## IDE

This project comes with an IntelliJ IDEA setup
that was tested with version 2025.1.4.1.


## Building

No jar file build is necessary.

To create a native image:

* Run the jar file first and make sure to exercise the GUI.
GraalVM's build tool does a good job at statically determining
what classes and methods are called,
but that is static analysis and it has its limits.  
Running the jar file with the parameters seen in [build.gradle.kts]
makes it record any calls that have escaped analysis,
such as classes loaded with `Class.forName(String)` and similar.
* Do `jh ./gradlew nativeCompile`.

_On Windows, leave the `./` part out._

**Note:** This build currently fails, with the error message
`unbalanced monitors - locked objects do not match`.

"Monitor" is bytecode speak for "lock", which happens at the Java level in
`synchronized` blocks, and at the JVM for some internal operations (including class loading/initialization IIRC).  
Unbalanced monitors are typically associated with exceptions.  

The root cause is that the unbalanced monitors check is *optional*.  
The JVM can deal with unbalanced monitors,
which has been described as "runtime trickery using exception tables";
AOT compilers can't do it, and if they don't report the situation,
they risk run-time crashes.

There's a [suggestion to fix this on the JIDE Software forum](https://www.jidesoft.com/forum/viewtopic.php?f=4&t=17379&p=85805#p85805).

A typical error message is:

```
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [_,_,_,_,_,_,_,_,_,_,_,_,_] stack: [] locks: [533 / 32]]
Other frame state: [locals: [_,_,_,_,_,_,_,_,_,_,_,_,_] stack: [] locks: []]
Parser context: com.jidesoft.pane.FloorTabbedPane.insertTab(Unknown Source) [bci: 253, intrinsic: false]
 253: goto          264
 256: astore        12
 258: aload         7
 260: monitorexit   
 261: aload         12
 263: athrow       

Call path from entry point to com.jidesoft.pane.FloorTabbedPane.insertTab(String, Icon, Component, String, int):
   at com.jidesoft.pane.FloorTabbedPane.insertTab(Unknown Source)
   at javax.swing.JTabbedPane.addTab(JTabbedPane.java:802)
   at com.jidesoft.docking.FrameContainer.addTab(Unknown Source)
   at com.jidesoft.docking.DefaultDockingManager.a(Unknown Source)
   at com.jidesoft.docking.DefaultDockingManager$r.run(Unknown Source)
   at java.lang.Thread.runWith(Thread.java:1596)
   at java.lang.Thread.run(Thread.java:1583)
   at com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:902)
```

## Running

To run the jar file, do `jh ./gradlew run`.  
To run the native image, do `jh ./gradlew nativeRun`.

_On Windows, leave the `./` part out._

## Benchmarking

Run `jh ./benchmark`.

_On Windows, leave the `./` part out._

The benchmark does not do the recording tasks that `jh ./gradlew run` does.  
However, benchmarking the running time of a Swing application
is a somewhat dubious idea at best;
you'll likely want to exercise some scripted sequence of user interaction.

I did a GUI tool testing review a few years ago,
and all except [QF-Test](https://www.qftest.com)
had pretty lackluster Swing support.  
QF-Test was easy to use and reliable in Swing, so it stood out.  
Downsides:
It's [not free](https://www.qftest.com/en/product/pricing.html),
and addressing `JTable` cells was clunky for some use cases,
but that was about all we saw.    
<small>This tiny review is not sponsored by Quality First Software.</small>
