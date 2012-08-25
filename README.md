Implementation of confabulation theory at the symbolic level and experiments
========

Implementation of different sentence completion architectures using
<a href="http://www.scholarpedia.org/article/Confabulation_theory_%28computational_intelligence%29">confabulation</a>
, module hierarchies and multiconfabulation using a symbol-level model of modules and knowledge links. 

This implementation is initially the product of a master thesis by Bernard
Paulus and Cédric Snauwaert, relased under the GPL - see LICENSE.txt .

Each of the directories here correspond to an eclipse project. See 
Setup, build and run with Eclipse
for an overview on how tho run them.

Overview of the different programs
==================================

Here are the programs that might be interesting:

    .
    |-- java_corpus_preprocessor
    |   `-- src
    |       `-- Main.java
    `-- java_sentence_completion
        `-- src
            |-- colt
            |       `-- SparseMatricesBenchmarks.java
            `-- confabulation
                |-- Main.java
                `-- tests
                    `-- BatchCompletionTest.java

In java\_corpus\_preprocessor, Main.java pre-processes a corpus UTF8 text file
into a form suitable for the sentence completion program. It opens a GUI to
request the location of the file to pre-process.

In java\_sentence\_completion,

*   Main.java is the main sentence completion program. It is
    [REPL](https://en.wikipedia.org/wiki/REPL) that completes the sentences that
    are inputted in the command line.
*   SparseMatricesBenchmarks.java is the file where we carried out our test to
    check whether it was appropriate to walk away of parallelcolt.
    All that files in that packages are the only ones left that need parallelcolt.
*   BatchCompletionTest.java is the program that runs multiple completions at
    once. It was the one used to generate the example in the chapter 5 of our
    master thesis.


Setup, build and run with Eclipse
=================================

Here are the instructions to set up and run the sentence completion project with
Eclipse

Prerequisites
-------------

You need

*   At least 1.5 GB of RAM for the intermediary sized corpus 
    (mille et une nuits).
    Architectures with less RAM are can still run, but for smaller corpus's.
*   An installation of Eclipse IDE at least Indigo, with JUnit4, downloadable
    as a single program [here](http://eclipse.org/downloads/) (download the
    "classic" version)
*   Some preprocessed corpus files. We uploaded some
    [here](https://github.com/downloads/confabulation/symbolic/corpus_samples.zip).

Setup
------


1.  Launch eclipse and start a new project
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse0.png)
2.  Create the project and give java\_sentence\_completion as the project
    location
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse1.png)
3.  Click next and open the libraries tab
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse2.png)
4.  Add java\_sentence\_completion/src/parallelcolt-0.9.4.jar to the set of
    external libraries. This is required to compile and run the matrix
    benchmarks.
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse3.png)
5.  Add JUnit4 to the project libraries
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse4.png)
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse5.png)
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse6.png)
6.  Click finish.
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse7.png)

Setup: done!

Build and run
=============

This assumes you have your corpus preprocessed / unzipped from the above
archive.

7.  Open the Main.java file, and click on the build and run button
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse8.png)
    The program will open a dialog to choose the preprocessed corpus file.
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse9.png)
8.  Select your preprocessed corpus file. **Beware:** if you plan to use an
    intermediary-sized corpus, like the full text of "les contes des mille et
    une nuits", apply first the next step first.
    ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse10.png)
9.  If you run the project with a corpus that necessitates too much memory, it
    crashes and prints the following message

        Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
            ...

    To solve this problem, we will rise the limit on memory usage of the Java virtual machine.

    1.  go to the properties of Main.java
        ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse11.png)
    2.  open the run/debug settings, and click "edit"
        ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse12.png)
    3.  Augment the maximal memory usage allowed for the JVM by inserting

            -Xmx1000m

        in the VM field of the Argument tab
        ![new project](https://github.com/downloads/confabulation/symbolic/setup_eclipse13.png)
        This allows the JVM to use up to 1000 MB of memory.
        Launch Main.java again.

10. You are done!

