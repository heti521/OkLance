package com.dongnao.lance.compiler;

import com.dongnao.lance.api.OkLance;
import com.dongnao.lance.api.annotation.BaseUrl;
import com.dongnao.lance.api.annotation.GET;
import com.dongnao.lance.api.annotation.POST;
import com.dongnao.lance.api.annotation.Parameter;
import com.dongnao.lance.api.annotation.ParameterMap;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import okhttp3.HttpUrl;

/**
 * @author Lance
 * @date 2018/5/20
 */

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.dongnao.lance.api.annotation.BaseUrl")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RetrofitProcessor extends AbstractProcessor {


    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> baseUrlElement = roundEnvironment
                .getElementsAnnotatedWith(BaseUrl.class);
        if (!baseUrlElement.isEmpty()) {
            processBaseUrl(baseUrlElement);
            return true;
        }
        return false;
    }

    private void processBaseUrl(Set<? extends Element> baseUrlElement) {
        //Map<String,String> 类型
        ParameterizedTypeName map = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(String.class)

        );

        for (Element element : baseUrlElement) {

            //因为BaseUrl只能在Type类上 这里肯定是TypeElement
            TypeElement typeElement = (TypeElement) element;
            //描述类名的对象
            ClassName className = ClassName.get(typeElement);
            //创建接口实现类
            TypeSpec.Builder typeSpecBuilder = TypeSpec
                    .classBuilder(typeElement.getSimpleName() + "Impl")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(className);

            //获得baseUrl注解的值
            String baseUrl = element.getAnnotation(BaseUrl.class).value();

            //获得所有子节点 类中的子节点包括(函数、成员属性等)
            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
            for (Element enclosedElement : enclosedElements) {
                // 属于 类或接口的函数 get/post
                if (enclosedElement instanceof ExecutableElement) {
                    ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                    String url;
                    GET get = executableElement.getAnnotation(GET.class);
                    POST post = executableElement.getAnnotation(POST.class);
                    String method;
                    //用于日志打印
                    String METHOD = "[" + typeElement.getQualifiedName() + "+executableElement" +
                            ".getSimpleName()]";
                    if (null != get) {
                        method = "get";
                        url = get.value();
                    } else if (null != post) {
                        method = "post";
                        url = post.value();
                    } else {
                        throw new RuntimeException(METHOD + " Not Specified Http " +
                                "Method (GET or " +
                                "POST)");
                    }

                    // 实现/重写 对应的函数
                    MethodSpec.Builder methodBuilder = MethodSpec.overriding(executableElement)
                            //HttpUrl httpUrl = HttpUrl.parse(baseUrl).newBuilder(url).build();
                            .addStatement("$T httpUrl = $T.parse($S).newBuilder($S).build()",
                                    HttpUrl.class,
                                    HttpUrl.class,
                                    baseUrl,
                                    url)
                            // Map<String,String> params = new HashMap<>();
                            .addStatement("$T params = new $T<>()", map, HashMap.class);

                    //函数的参数
                    for (VariableElement variableElement : executableElement.getParameters()) {
                        //参数名 city
                        Name simpleName = variableElement.getSimpleName();
                        //参数的注解
                        Parameter parameter = variableElement.getAnnotation(Parameter.class);
                        ParameterMap parameterMap = variableElement.getAnnotation(ParameterMap
                                .class);
                        if (null != parameter) {
                            //params.put("city",city)
                            methodBuilder.addStatement("params.put($S,$N)", parameter.value(),
                                    simpleName);
                        } else if (null != parameterMap) {
                            //params.putAll();
                            methodBuilder.addStatement("params.putAll($N)", simpleName);
                        } else {
                            throw new RuntimeException(METHOD + " Parameter [" + simpleName + "] " +
                                    "Not Specified Type " +
                                    "(Parameter or " +
                                    "ParameterMap)");
                        }
                    }
                    methodBuilder.addStatement("return $T.getInstance().$L(httpUrl,params)",
                            OkLance.class, method);
                    typeSpecBuilder.addMethod(methodBuilder.build());
                }
            }

            JavaFile javaFile = JavaFile.builder(className.packageName(), typeSpecBuilder.build())
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void log(CharSequence charSequence) {
        messager.printMessage(Diagnostic.Kind.WARNING, charSequence);
    }
}
