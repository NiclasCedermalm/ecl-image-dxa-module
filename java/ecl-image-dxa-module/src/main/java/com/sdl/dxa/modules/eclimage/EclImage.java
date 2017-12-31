package com.sdl.dxa.modules.eclimage;

import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;

import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.img;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * ECL Image
 */
@Slf4j
public abstract class EclImage extends EclItem {

    static final String ECL_STUB_SCHEMA_PREFIX = "ExternalContentLibraryStubSchema";

    static private EclImageConfiguration configuration;

    /**
     * Set configuration
     * @param configuration
     */
    public static void setConfiguration(EclImageConfiguration configuration) {
        EclImage.configuration = configuration;
    }

    /**
     * Build up an ECL image sub class for specified ECL name
     * @param eclName
     * @param semanticMappingRegistry
     * @throws DxaException
     */
    public static void buildAndRegisterSubType(String eclName, SemanticMappingRegistry semanticMappingRegistry) throws DxaException {

        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new LoaderClassPath(EclImage.class.getClassLoader()));

            // Create sub class to this class and add the needed semantic annotations for the ECL type
            //
            CtClass subClass = pool.makeClass(EclImage.class.getPackage().getName() + "." + eclName);
            final CtClass superClass = pool.get(EclImage.class.getName());
            subClass.setSuperclass(superClass);
            subClass.setModifiers(Modifier.PUBLIC);

            ClassFile ccFile = subClass.getClassFile();
            ConstPool cp = ccFile.getConstPool();
            AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
            Annotation as = new Annotation("com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity", cp);
            as.addMemberValue("entityName", new StringMemberValue(ECL_STUB_SCHEMA_PREFIX + eclName.toLowerCase(), cp));
            as.addMemberValue("vocabulary", new StringMemberValue("http://www.sdl.com/web/schemas/core", cp));
            as.addMemberValue("prefix", new StringMemberValue("s", cp));
            attr.addAnnotation(as);
            ccFile.addAttribute(attr);

            semanticMappingRegistry.registerEntity(subClass.toClass(Thread.currentThread().getContextClassLoader(), null));

        }
        catch ( Exception e ) {
            throw new DxaException("Could not create ECL image subclass for '" + eclName + "'", e);
        }

    }

    @Override
    public boolean isImage() {
        return true;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {

        if ( configuration.isUseTemplateFragment() && !isEmpty(super.getTemplateFragment()) ) {
            return super.toHtmlElement(widthFactor, aspect, cssClass, containerSize, contextPath);
        }
        if (isEmpty(getUrl())) {
            log.warn("Skipping image with empty URL: {}", this);
            throw new DxaException("URL is null for image component: " + this);
        }

        String url = getUrl();

        // Right now this is connected to the DXA Whitelabel design.
        //
        return img(url.startsWith("/") && configuration.isGenerateResponsiveImages() ? getMediaHelper().getResponsiveImageUrl(url, widthFactor, aspect, containerSize) : url)
                .withClass(cssClass)
                .withAttribute("data-aspect", String.valueOf(Math.round(aspect * 100) / 100))
                .withAttribute("width", widthFactor)
                .build();
    }

    @Override
    public MvcData getMvcData() {
        return MvcDataCreator.creator()
                .fromQualifiedName("ECLImage:Entity:Image")
                .defaults(DefaultsMvcData.CORE_ENTITY)
                .create();
    }
}
