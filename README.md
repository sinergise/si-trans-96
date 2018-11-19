# si-trans-96
Java library to transform coordinates in Slovenia between MGI D48/GK and ETRS89 D96/TM

# Usage
Include `si-trans-96.jar` in your classpath.
Call a transformation method on the `SiTrans96` class for each coordinate that needs to be transformed.

Both forward and inverse transformations between D48/GK and D96/TM are available: 
 - `SiTrans96.d96tm_to_d48gk`
 - `SiTrans96.d48gk_to_d96tm`
 
Best performance is achieved
by consecutively transforming points that are close together—this
will allow reuse of the cached last-used triangle.
In a multi-threaded setting, caching is done for each thread separately.

# Building
Use Gradle or Eclipse with Gradle plugin installed, then execute the `assemble` task.
