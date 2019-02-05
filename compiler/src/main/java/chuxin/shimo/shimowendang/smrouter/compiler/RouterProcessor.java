package chuxin.shimo.shimowendang.smrouter.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import chuxin.shimo.shimowendang.smrouter.annotation.Module;
import chuxin.shimo.shimowendang.smrouter.annotation.Modules;
import chuxin.shimo.shimowendang.smrouter.annotation.Router;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
    private static final String BR = "\n";
    private final String MODULE_PREFIX = "RouterMapping";
    private final String ROUTERINIT_CLASS_NAME = "RouterInit";
    private static final String ROUTERS_CLASS_NAME = "Routers";
    private static final String EXTRATYPES_CLASS_NAME = "ExtraTypes";
    private static final String EXTRATYPES_PARAM_NAME = "extraTypes";
    private static final String METHOD_INIT = "init";
    private static final String PACKAGE_NAME_ROUTER = "chuxin.shimo.shimowendang.smrouter";
    private static final String PACKAGE_NAME_ROUTER_CORE = "chuxin.shimo.shimowendang.smrouter.core";
    private static final String PACKAGE_NAME_ROUTER_INTECEPTOR = "chuxin.shimo.shimowendang.smrouter.interfaces" +
        ".Interceptor";
    private static final String PACKAGE_EXTRATYPES = PACKAGE_NAME_ROUTER_CORE.concat(".").concat(EXTRATYPES_CLASS_NAME);
    private static final String NEW_ARRAYLIST_OF_INTERCEPTOR =
        "java.util.List<".concat(PACKAGE_NAME_ROUTER_INTECEPTOR).concat(">  interceptors " +
            "= new java.util.ArrayList();");
    private static final boolean DEBUG = true;
    private Filer mFiler;
    private Logger mLogger;
    private Elements mElements;
    private Types mTypes;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mTypes = processingEnv.getTypeUtils();
        mLogger = new Logger(processingEnv.getMessager());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> ret = new HashSet<>();
        ret.add(Modules.class.getCanonicalName());
        ret.add(Module.class.getCanonicalName());
        ret.add(Router.class.getCanonicalName());
        return ret;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            debug("process apt is null");
            return false;
        }
        debug("process apt with " + annotations.toString());
        boolean hasModule = false;
        boolean hasModules = false;
        // module
        String moduleName = MODULE_PREFIX;
        Set<? extends Element> moduleList = roundEnv.getElementsAnnotatedWith(Module.class);
        if (moduleList != null && moduleList.size() > 0) {
            Module annotation = moduleList.iterator().next().getAnnotation(Module.class);
            moduleName = moduleName + "_" + annotation.value();
            hasModule = true;
        }
        // modules
        String[] moduleNames = null;
        Set<? extends Element> modulesList = roundEnv.getElementsAnnotatedWith(Modules.class);
        if (modulesList != null && modulesList.size() > 0) {
            Element modules = modulesList.iterator().next();
            moduleNames = modules.getAnnotation(Modules.class).value();
            hasModules = true;
        }
        // RouterInit
        if (hasModules) {
            debug("generate modules RouterInit");
            generateModulesRouterInit(moduleNames);
        } else if (!hasModule) {
            debug("generate default RouterInit");
            generateDefaultRouterInit();
        }
        // RouterMapping
        return handleRouter(moduleName, roundEnv);
    }

    private void generateDefaultRouterInit() {
        MethodSpec.Builder initMethod = MethodSpec.methodBuilder(METHOD_INIT)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        initMethod.addStatement(MODULE_PREFIX.concat(".map()"));
        TypeSpec routerInit = TypeSpec.classBuilder(ROUTERINIT_CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(initMethod.build())
            .build();
        try {
            JavaFile.builder(PACKAGE_NAME_ROUTER_CORE, routerInit)
                .build()
                .writeTo(mFiler);
        } catch (Exception e) {
            error(e.getMessage());
        }
        debug("generate default RouterInit end");
    }

    private void generateModulesRouterInit(String[] moduleNames) {
        MethodSpec.Builder initMethod = MethodSpec.methodBuilder(METHOD_INIT)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        for (String module : moduleNames) {
            initMethod.addStatement(MODULE_PREFIX.concat("_").concat(module).concat(".map()"));
        }
        TypeSpec routerInit = TypeSpec.classBuilder(ROUTERINIT_CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(initMethod.build())
            .build();
        try {
            JavaFile.builder(PACKAGE_NAME_ROUTER_CORE, routerInit)
                .build()
                .writeTo(mFiler);
        } catch (Exception e) {
            error(e.getMessage());
        }
        debug("generate modules RouterInit end");
    }

    private boolean handleRouter(String genClassName, RoundEnvironment roundEnv) {
        debug("handleRouter start");
        MethodSpec.Builder mapMethod = MethodSpec.methodBuilder("map")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            //"chuxin.shimo.shimowendang.smrouter.core.ExtraTypes extraTypes"
            .addStatement(PACKAGE_EXTRATYPES
                .concat(" ")
                .concat(EXTRATYPES_PARAM_NAME))
            .addCode(BR);

        mapMethod = initRouters(roundEnv, mapMethod);
        TypeSpec build = TypeSpec.classBuilder(genClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(mapMethod.build())
            .build();
        try {
            JavaFile.builder(PACKAGE_NAME_ROUTER_CORE, build)
                .build()
                .writeTo(mFiler);
        } catch (Throwable e) {
            error(e.getMessage());
        }
        debug("handleRouter end");
        return true;
    }

    private Builder initRouters(RoundEnvironment roundEnv, Builder mapMethod) {
        debug("initRouters start");
        Set<? extends Element> routerElements = roundEnv.getElementsAnnotatedWith(Router.class);
        debug("initRouters router size=" + routerElements.size());
        mapMethod.addStatement(NEW_ARRAYLIST_OF_INTERCEPTOR)
            .addCode(BR);
        for (Element routerElement : routerElements) {
            debug(">>>>>>>>>>>>>>>>>>>router:" + routerElement.getSimpleName());
            //检测类型
            ClassName className;
            if (routerElement.getKind() == ElementKind.CLASS) {
                className = ClassName.get((TypeElement) routerElement);
            } else {
                throw new IllegalArgumentException("unknow type");
            }
            //初始化属性
            Router router = routerElement.getAnnotation(Router.class);
            //"extraTypes = new chuxin.shimo.shimowendang.smrouter.core.ExtraTypes()"
            mapMethod.addStatement(EXTRATYPES_PARAM_NAME.concat(" = ")
                .concat("new ")
                .concat(PACKAGE_EXTRATYPES)
                .concat("()"));
            //装配数据
            addStatement(mapMethod, int.class, router.intParams());
            addStatement(mapMethod, long.class, router.longParams());
            addStatement(mapMethod, boolean.class, router.booleanParams());
            addStatement(mapMethod, short.class, router.shortParams());
            addStatement(mapMethod, float.class, router.floatParams());
            addStatement(mapMethod, double.class, router.doubleParams());
            addStatement(mapMethod, byte.class, router.byteParams());
            addStatement(mapMethod, char.class, router.charParams());
            addStatement(mapMethod, int.class, new String[]{router.flag()});
            //加载拦截器
            mapMethod.addStatement("interceptors.clear();");
            List<? extends TypeMirror> types = getTypeMirrorFromAnnotationValue(router::interceptors);
            List<String> interceptorNames = new ArrayList<>();
            if (types != null) {
                debug("initRouters inteceptor size=" + types.size());
                for (TypeMirror typeMirror :
                    types) {
                    final String interceptorName = typeMirror.toString();
                    if (isValidClass((TypeElement) ((DeclaredType) typeMirror).asElement())) {
                        if (interceptorNames.contains(interceptorName)) {
                            throw new RuntimeException("initRouters, inteceptor name = " + interceptorName +
                                " is multiple with ".concat(className.simpleName()));
                        }
                        interceptorNames.add(interceptorName);
                        mapMethod.addStatement("interceptors.add(new " + interceptorName + "());");
                        debug("initRouters inteceptor name =" + interceptorName);
                    } else {
                        throw new RuntimeException("initRouters, inteceptor name = " + interceptorName +
                            " is not implements ".concat(PACKAGE_NAME_ROUTER_INTECEPTOR));
                    }
                }
            } else {
                debug("initRouters inteceptor is null");
            }
            //加载路由
            debug("initRouters router's url size=" + router.value().length);
            for (String format : router.value()) {
                debug("initRouters url name = " + format);
                mapMethod.addStatement(
                    //"chuxin.shimo.shimowendang.smrouter.Routers.map($S, $T.class, null, extraTypes,interceptors)"
                    PACKAGE_NAME_ROUTER_CORE.concat(".")
                        .concat(ROUTERS_CLASS_NAME)
                        .concat(".map($S, $T.class, extraTypes, interceptors)"), format, className);
            }
            mapMethod.addCode(BR);
            debug("<<<<<<<<<<<<<<<<<<<<end:" + routerElement.getSimpleName());
        }
        debug("initRouters end");
        return mapMethod;
    }

    //检测是否实现了拦截器接口
    private boolean isValidClass(TypeElement classTypeElement) {
        for (TypeMirror typeMirror :
            classTypeElement.getInterfaces()) {
            if (typeMirror.toString().equals(PACKAGE_NAME_ROUTER_INTECEPTOR)) {
                return true;
            }
        }
        TypeMirror superClassType = classTypeElement.getSuperclass();
        if (superClassType == null || superClassType.getKind() == TypeKind.NONE) {
            ////已经追溯到 java.lang.Object 类型
            return false;
        }
        return isValidClass((TypeElement) mTypes.asElement(superClassType));
    }

    private void addStatement(MethodSpec.Builder mapMethod, Class typeClz, String[] args) {
        String extras = join(args);
        if (extras.length() > 0) {
            String typeName = typeClz.getSimpleName();
            String s = typeName.substring(0, 1).toUpperCase() + typeName.replaceFirst("\\w", "");

            mapMethod.addStatement("extraTypes.set" + s + "Extra($S.split(\",\"))", extras);
        }
    }

    @FunctionalInterface
    public interface GetClassValue {
        void execute() throws MirroredTypesException;
    }

    private List<? extends TypeMirror> getTypeMirrorFromAnnotationValue(GetClassValue c) {
        try {
            c.execute();
        } catch (MirroredTypesException ex) {
            return ex.getTypeMirrors();
        }
        return null;
    }

    private String join(String[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        if (args.length == 1) {
            return args[0];
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            sb.append(args[i]).append(",");
        }
        sb.append(args[args.length - 1]);
        return sb.toString();
    }

    private void error(String error) {
        mLogger.info(">>>" + error);
    }

    private void debug(String msg) {
        if (DEBUG) {
            mLogger.info(">>>" + msg);
        }
    }
}
