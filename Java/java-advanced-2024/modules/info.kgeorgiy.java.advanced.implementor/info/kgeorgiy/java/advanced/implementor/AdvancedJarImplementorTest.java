package info.kgeorgiy.java.advanced.implementor;

import info.kgeorgiy.java.advanced.implementor.full.lang.Arabic;
import info.kgeorgiy.java.advanced.implementor.full.lang.Greek;
import info.kgeorgiy.java.advanced.implementor.full.lang.Hebrew;
import info.kgeorgiy.java.advanced.implementor.full.lang.Russian;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedJarImplementorTest extends AdvancedImplementorTest {
    public AdvancedJarImplementorTest() {
    }

    @Test
    @Override
    public void test01_constructor() {
        assertConstructor(Impler.class, JarImpler.class);
    }

    @Test
    public void test21_encoding() {
        testOk(
                Arabic.class, Arabic.\u0645\u0631\u062d\u0628\u0627.class,
                Hebrew.class, Hebrew.\u05d4\u05d9.class,
                Greek.class, Greek.\u03b3\u03b5\u03b9\u03b1.class,
                Russian.class, Russian.\u041f\u0440\u0438\u0432\u0435\u0442.class
        );
    }

    @Override
    protected void implement(final Path root, final Impler implementor, final Class<?> clazz) throws ImplerException {
        super.implement(root, implementor, clazz);
        InterfaceJarImplementorTest.implementJar(root, implementor, clazz);
    }
}
