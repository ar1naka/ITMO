#!/bin/bash
javac -d tmp -cp ../../java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar ../java-solutions/info/kgeorgiy/ja/evseeva/implementor/Implementor.java
jar -cfm Implementor.jar MANIFEST.MF -C tmp info/kgeorgiy/ja/evseeva/implementor/Implementor.class
