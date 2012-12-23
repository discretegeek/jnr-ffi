package jnr.ffi.provider.jffi;

import jnr.ffi.*;
import jnr.ffi.byref.ByReference;
import jnr.ffi.mapper.*;
import jnr.ffi.mapper.FromNativeType;
import jnr.ffi.mapper.ToNativeType;
import jnr.ffi.provider.converters.EnumConverter;
import jnr.ffi.provider.ParameterFlags;
import jnr.ffi.provider.converters.*;

import java.util.EnumSet;
import java.util.Set;

import static jnr.ffi.provider.jffi.AsmUtil.isDelegate;
import static jnr.ffi.provider.jffi.InvokerUtil.getNativeType;
import static jnr.ffi.provider.jffi.NumberUtil.sizeof;

final class InvokerTypeMapper extends AbstractSignatureTypeMapper implements SignatureTypeMapper {
    private final NativeClosureManager closureManager;
    private final AsmClassLoader classLoader;

    public InvokerTypeMapper(NativeClosureManager closureManager, AsmClassLoader classLoader) {
        this.closureManager = closureManager;
        this.classLoader = classLoader;
    }

    public FromNativeConverter getFromNativeConverter(SignatureType signatureType, FromNativeContext fromNativeContext) {
        FromNativeConverter converter;

        if (Enum.class.isAssignableFrom(signatureType.getDeclaredType())) {
            return EnumConverter.getInstance(signatureType.getDeclaredType().asSubclass(Enum.class));

        } else if (Struct.class.isAssignableFrom(signatureType.getDeclaredType())) {
            if (NativeLibraryLoader.ASM_ENABLED) {
                return AsmStructByReferenceFromNativeConverter.newStructByReferenceConverter(fromNativeContext.getRuntime(), signatureType.getDeclaredType().asSubclass(Struct.class),
                        ParameterFlags.parse(fromNativeContext.getAnnotations()), classLoader);
            } else {
                return jnr.ffi.provider.converters.StructByReferenceFromNativeConverter.getInstance(signatureType.getDeclaredType(), fromNativeContext);
            }

        } else if (closureManager != null && isDelegate(signatureType.getDeclaredType())) {
            return ClosureFromNativeConverter.getInstance(fromNativeContext.getRuntime(), signatureType, classLoader, this);

        } else if (NativeLong.class == signatureType.getDeclaredType()) {
            return NativeLongConverter.getInstance();

        } else if (String.class == signatureType.getDeclaredType() || CharSequence.class == signatureType.getDeclaredType()) {
            return StringResultConverter.getInstance(fromNativeContext);

        } else if ((Set.class == signatureType.getDeclaredType() || EnumSet.class == signatureType.getDeclaredType()) && (converter = EnumSetConverter.getFromNativeConverter(signatureType, fromNativeContext)) != null) {
            return converter;

        } else {
            return null;
        }

    }

    public ToNativeConverter getToNativeConverter(SignatureType signatureType, ToNativeContext context) {
        Class javaType = signatureType.getDeclaredType();
        ToNativeConverter converter;

        if (Enum.class.isAssignableFrom(javaType)) {
            return EnumConverter.getInstance(javaType.asSubclass(Enum.class));

        } else if (Set.class.isAssignableFrom(javaType) && (converter = EnumSetConverter.getToNativeConverter(signatureType, context)) != null) {
            return converter;

        } else if (isDelegate(javaType)) {
            return closureManager.newClosureSite(javaType);

        } else if (ByReference.class.isAssignableFrom(javaType)) {
            return ByReferenceParameterConverter.getInstance(context);

        } else if (Struct.class.isAssignableFrom(javaType)) {
            return StructByReferenceToNativeConverter.getInstance(context);

        } else if (NativeLong.class.isAssignableFrom(javaType)) {
            return NativeLongConverter.getInstance();

        } else if (StringBuilder.class.isAssignableFrom(javaType)) {
            return StringBuilderParameterConverter.getInstance(ParameterFlags.parse(context.getAnnotations()), context);

        } else if (StringBuffer.class.isAssignableFrom(javaType)) {
            return StringBufferParameterConverter.getInstance(ParameterFlags.parse(context.getAnnotations()), context);

        } else if (CharSequence.class.isAssignableFrom(javaType)) {
            return CharSequenceParameterConverter.getInstance(context);

        } else if (Byte[].class.isAssignableFrom(javaType)) {
            return BoxedByteArrayParameterConverter.getInstance(context);

        } else if (Short[].class.isAssignableFrom(javaType)) {
            return BoxedShortArrayParameterConverter.getInstance(context);

        } else if (Integer[].class.isAssignableFrom(javaType)) {
            return BoxedIntegerArrayParameterConverter.getInstance(context);

        } else if (Long[].class.isAssignableFrom(javaType)) {
            return sizeof(getNativeType(context.getRuntime(), javaType.getComponentType(), context.getAnnotations())) == 4
                ? BoxedLong32ArrayParameterConverter.getInstance(context)
                : BoxedLong64ArrayParameterConverter.getInstance(context);

        } else if (NativeLong[].class.isAssignableFrom(javaType)) {
            return sizeof(getNativeType(context.getRuntime(), javaType.getComponentType(), context.getAnnotations())) == 4
                    ? NativeLong32ArrayParameterConverter.getInstance(context)
                    : NativeLong64ArrayParameterConverter.getInstance(context);

        } else if (Float[].class.isAssignableFrom(javaType)) {
            return BoxedFloatArrayParameterConverter.getInstance(context);

        } else if (Double[].class.isAssignableFrom(javaType)) {
            return BoxedDoubleArrayParameterConverter.getInstance(context);

        } else if (Boolean[].class.isAssignableFrom(javaType)) {
            return BoxedBooleanArrayParameterConverter.getInstance(context);

        } else if (javaType.isArray() && Pointer.class.isAssignableFrom(javaType.getComponentType())) {
            return context.getRuntime().addressSize() == 4
                    ? Pointer32ArrayParameterConverter.getInstance(context)
                    : Pointer64ArrayParameterConverter.getInstance(context);

        } else if (long[].class.isAssignableFrom(javaType) && sizeof(getNativeType(context.getRuntime(), javaType.getComponentType(), context.getAnnotations())) == 4) {
            return Long32ArrayParameterConverter.getInstance(context);

        } else if (javaType.isArray() && Struct.class.isAssignableFrom(javaType.getComponentType())) {
            return StructArrayParameterConverter.getInstance(context, javaType.getComponentType());

        } else if (javaType.isArray() && String.class.isAssignableFrom(javaType.getComponentType())) {
            return StringArrayParameterConverter.getInstance(context);

        } else {
            return null;
        }
    }


    @Override
    public FromNativeType getFromNativeType(SignatureType type, FromNativeContext context) {
        return FromNativeTypes.create(getFromNativeConverter(type, context));
    }

    @Override
    public ToNativeType getToNativeType(SignatureType type, ToNativeContext context) {
        return ToNativeTypes.create(getToNativeConverter(type, context));
    }
}
