package com.sdl.dxa.modules.eclimage;

import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.views.AbstractInitializer;
import com.sdl.webapp.common.api.mapping.views.ModuleInfo;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModel;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModels;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.StringTokenizer;

/**
 * ECL Image Module initializer
 */
@Slf4j
@Component
@ModuleInfo(name = "ECLImage", areaName = "ECLImage", description = "Module to handle ECL images in a generic way")
public class EclImageModuleInitializer extends AbstractInitializer {

    @Value("${ecl.image.types}")
    private String eclImageTypes;

    @Value("${ecl.image.useTemplateFragment:false}")
    private boolean useTemplateFragment;

    @Value("${ecl.image.generateResponsiveImages:false}")
    private boolean generateResponsiveImages;

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    @PostConstruct
    public void initialize() throws DxaException {

        log.info("Initializing the ECL Image module...");
        StringTokenizer tokenizer = new StringTokenizer(eclImageTypes, ", ");
        while ( tokenizer.hasMoreTokens() ) {
            String eclName = tokenizer.nextToken();
            log.info("Creating ECL Image type for: " + eclName);
            EclImage.buildAndRegisterSubType(eclName, semanticMappingRegistry);
        }

        // Set configuration
        //
        EclImageConfiguration configuration = new EclImageConfiguration();
        configuration.setUseTemplateFragment(useTemplateFragment);
        configuration.setGenerateResponsiveImages(generateResponsiveImages);
        EclImage.setConfiguration(configuration);
    }

    @Override
    protected String getAreaName() {
        return "ECLImage";
    }


}
