package info.kgeorgiy.ja.evseeva.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Class Implementor implements the interfaces Impler and JarImpler.
 * It offers methods for generating an implementation for a given interface
 * and for creating a .java or .jar files.
 *
 * @author Evseeva Arina
 */
public class Implementor implements JarImpler {
    /**
     * Default constructor
     */
    public Implementor() {}
    /**
     * Main method which implements interface for implementor or jar-implementor
     * If args[0] equals "-jar", args[1] should be class name of the interface to implement,
     * args[2] should exist and be path for the jar-outputFile
     * If args length less than 3, args[0] should be token class, args[1] should be path for implementation class
     *
     * @param args   args[0]: "-jar" for generating a Jar file, class name otherwise.
     *  *            args[1]: Class name to be implemented.
     *  *            args[2]: Path for the implementation output (if generating Jar).
     */
    public static void main(String[] args)  {
        if (args == null || args.length > 3) {
            System.err.println("All arguments should be entered!");
            return;
        } else if (args[0] == null || args[1] == null) {
            System.err.println("You should enter at list 2 arguments!");
        }

        try {
            Implementor implementor = new Implementor();
            if (args[0].equals("-jar") && args[2] != null) {
                implementor.implementJar(Class.forName(args[1]), Paths.get(args[2]));
            } else {
                implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
            }
        } catch (ImplerException e) {
            System.err.println("Error with implementing");
        } catch (ClassNotFoundException e){
            System.err.println("Class is not find");
        }
    }

    /**
     * Produces code implementing class or interface specified by provided {@code token}.
     * <p>
     * Generated class' name should be the same as the class name of the type token with {@code Impl} suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * {@code root} directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to {@code $root/java/util/ListImpl.java}
     *
     *
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be
     * generated.
     */
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (!token.isInterface() || Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Error with implementing this type of class");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(createPath(root, token))){
            writer.write(createHeaders(token));
            createMethods(token, writer);
            writer.write("}");
        } catch (IOException e){
            throw new ImplerException("Error with writer", e);
        }
    }

    /**
     * Produces <var>.jar</var> file implementing class or interface specified by provided <var>token</var>.
     * <p>
     * Generated class' name should be the same as the class name of the type token with <var>Impl</var> suffix
     * added.
     *
     * @param token type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException when implementation cannot be generated.
     */
// :NOTE: inheritDocs
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path p = jarFile.getParent();
        implement(token, p);
        compile(token, p);
        createJar(token, jarFile, p);
    }

    /**
     * Creates a JAR file that contains a compiled class presented by an object of the token.
     * @param token Token that should be added to JAR file.
     * @param jarFile Path to the JAR file to be created.
     * @param p Path to search for compiled files.
     */

    private void createJar(Class<?> token, Path jarFile, Path p) {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        String path = token.getPackageName().replace(".", "/") +
                "/" + getFullName(token) + ".class";
        try (JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(jarFile), manifest)){
            outputStream.putNextEntry(new ZipEntry(path));
            Files.copy(p.resolve(path), outputStream);
        } catch (IOException e) {
            System.err.println("Error in creating JAR-File!");
        }
    }

    /**
     * Generates path for the given class token in root directory.
     * @param token The token of the class for which the Path should be created.
     * @param root The root directory where the path will be placed.
     * @return A path representing the file location for this class token.
     */
    private Path getPath(Class<?> token, Path root) {
        return root.resolve(token.getPackageName().replace(".", File.separator)).
                resolve(getFullName(token) + ".java");
    }

    /**
     * Generates a string taking into account the encoding.
     * @param f String which needs encoding.
     * @return Result after encoding.
     */

    private String code(String f){
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < f.length(); i++) {
            if (f.charAt(i) < 128){
                tmp.append(f.charAt(i));
            } else {
                tmp.append(String.format("\\u%04x", (int) f.charAt(i)));
            }
        }
        return tmp.toString();
    }

    /**
     * Compiles given token.
     * @param token The class type to be compiled.
     * @param root Path to source files of the class.
     * @throws ImplerException If an error occurs during compilation.
     */
    private void compile(Class<?> token, Path root) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Could not find java compiler, include tools.jar to classpath");
        }
        final String classpath = root + File.pathSeparator + getClassPath(token);
        String file = getPath(token, root).toString();
        final String[] args = new String[]{file, "-cp", classpath, "-encoding", "UTF-8"};
        final int exitCode = compiler.run(null, null, null, args);
        if (exitCode != 0) {
            throw new ImplerException("Compiler exit code:" + exitCode);
        }
    }

    /**
     * Gets the class path of the specified class token.
     * @param token Token of the class for which needs to get the class path.
     * @return The class path as a String.
     */

    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Creates method implementations based on the methods defined in this class token
     * @param token The class token whose methods are being implemented.
     * @param writer BufferedWriter for writing method implementations
     * @throws IOException If an I/O error occurred while writing methods.
     */

    private void createMethods(Class<?> token, BufferedWriter writer) throws IOException {
        Method[] met = token.getMethods();
        for (Method m : met) {
            writer.write("\t" + getModifiers(m) + m.getReturnType().getCanonicalName() + " "
                    + code(m.getName()) + "(");
            writer.write(getParameters(m));
            writer.write(getExceptions(m));
            writer.write(" {" + System.lineSeparator() +
                    "\t\treturn " + getReturnValue(m) + ";" + System.lineSeparator() +
                    "\t}" + System.lineSeparator());
            writer.newLine();
        }
    }

    /**
     * Gets access modifiers to this method, and returns them as a formatted string.
     * @param m Method whose access modifiers need to take.
     * @return Formatted string with access modifiers of this method.
     */

    private String getParameters(Method m) {
        Parameter[] parameters = m.getParameters();
        String res = Arrays.stream(parameters)
                .map(parameter -> parameter.getType().getCanonicalName() + " " + parameter.getName())
                .collect(Collectors.joining(", "));
        return res + ")";
    }

    /**
     * Gets access modifiers to this method, and returns them as a formatted string.
     * @param m Method whose access modifiers need to take.
     * @return Formatted string with access modifiers of this method.
     */

    private String getModifiers(Method m) {
        int mod = m.getModifiers();
        StringBuilder res = new StringBuilder();
        if (Modifier.isPublic(mod)) {
            res.append("public ");
        } else if (Modifier.isStatic(mod)) {
            res.append("static ");
        }
        return res.toString();
    }

    /**
     * Gets exceptions to this method, and returns them as a formatted string.
     * @param m Method whose exceptions need to take.
     * @return Formatted string with exceptions of this method.
     */
    private String getExceptions(Method m) {
        Class<?>[] exceptions = m.getExceptionTypes();
        return Arrays.stream(exceptions)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", ", exceptions.length > 0 ? " throws " : "", ""));
    }

    /**
     * Generates the required headers for a class file on a given class token.
     * @param token The token of the class for which the headers are created.
     * @return A header line including the class declaration and package declaration.
     */

    private String createHeaders(Class<?> token) {
        String res = "public class " + code(getFullName(token)) + " implements "
                + code(token.getCanonicalName()) + " {" + System.lineSeparator();
        if (token.getPackage() != null) {
            return "package " + token.getPackageName() + ";" + System.lineSeparator() + res;
        } else {
            return res;
        }
    }

    /**
     * Create path for the specified class in the specified root directory.
     * @param root The root directory where the file should be created.
     * @param token The class token for which the Path should be created.
     * @return Path to the file to be created.
     * @throws ImplerException If an error occurred while creating the directory.
     */

    private Path createPath(Path root, Class<?> token) throws ImplerException {
        Path file = getPath(token, root);
        try {
            if (file.getParent() != null){
                Files.createDirectories(file.getParent());
            }
        } catch (IOException e) {
            throw new ImplerException("Error with creating the directory", e);
        }
        return file;
    }

    /**
     * Method generates the full name by appending "Impl" to the simple name of the given token.
     * @param token The object of the class for which the full name should be generated.
     * @return the full name by appending "Impl" to the simple name of the provided Class object
     */

    private String getFullName(Class<?> token){
        return token.getSimpleName() + "Impl";
    }

    /**
     * Create return value by defining type of given method
     * @param m The method for which the return value should be defined.
     * @return  - If return type is boolean: "false"
     *          - If return type is void: ""
     *          - If return type is a primitive type: "0"
     *          - For any other reference types: "null"
     */
    private String getReturnValue(Method m){
        if (m.getReturnType() == boolean.class) {
            return "false";
        } else if (m.getReturnType() == void.class ) {
            return "";
        } else if (m.getReturnType().isPrimitive()) {
            return "0";
        } else {
            return "null";
        }
    }
}
