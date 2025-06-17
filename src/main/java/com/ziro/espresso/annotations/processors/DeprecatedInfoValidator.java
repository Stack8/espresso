package com.ziro.espresso.annotations.processors;

import com.google.auto.service.AutoService;
import com.ziro.espresso.annotations.DeprecatedInfo;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.ziro.espresso.annotations.DeprecatedInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class DeprecatedInfoValidator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(DeprecatedInfo.class)) {
            DeprecatedInfo info = element.getAnnotation(DeprecatedInfo.class);

            if (info.deprecatedIn().trim().isEmpty()) {
                processingEnv
                        .getMessager()
                        .printMessage(Diagnostic.Kind.ERROR, "deprecatedIn must not be empty", element);
            }

            if (info.removingIn().trim().isEmpty()) {
                processingEnv
                        .getMessager()
                        .printMessage(Diagnostic.Kind.ERROR, "removingIn must not be empty", element);
            }

            if (element.getAnnotationMirrors().stream()
                    .noneMatch(mirror -> mirror.getAnnotationType().toString().equals("java.lang.Deprecated"))) {

                processingEnv
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR,
                                "@DeprecatedInfo must be used together with @Deprecated",
                                element);
            }
        }

        return false;
    }
}
