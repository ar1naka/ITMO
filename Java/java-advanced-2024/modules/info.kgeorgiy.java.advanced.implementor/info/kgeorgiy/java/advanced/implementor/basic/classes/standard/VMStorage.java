package info.kgeorgiy.java.advanced.implementor.basic.classes.standard;


/**
 * Copy of {@link jdk.internal.foreign.abi.VMStorage}
 *
 * @param type              the type of storage. e.g. stack, or which register type (GP, FP, vector)
 * @param segmentMaskOrSize the (on stack) size in bytes when type = stack, a register mask otherwise,
 *                          the register mask indicates which segments of a register are used.
 * @param indexOrOffset     the index is either a register number within a type, or
 *                          a stack offset in bytes if type = stack.
 *                          (a particular platform might add a bias to this in generate code)
 * @param debugName         the debug name
 */
public record VMStorage(byte type,
                        short segmentMaskOrSize,
                        int indexOrOffset,
                        String debugName) {

    public VMStorage(byte type, short segmentMaskOrSize, int indexOrOffset) {
        this(type, segmentMaskOrSize, indexOrOffset, "Stack@" + indexOrOffset);
    }
}
