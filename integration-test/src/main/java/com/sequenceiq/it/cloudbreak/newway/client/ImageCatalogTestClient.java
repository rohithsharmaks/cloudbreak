package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.mappable.CloudPlatform;
import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.imagecatalog.ImageCatalogCreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.imagecatalog.ImageCatalogCreateIfNotExistsAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.imagecatalog.ImageCatalogCreateWithoutNameLoggingAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.imagecatalog.ImageCatalogDeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.imagecatalog.ImageCatalogGetAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.imagecatalog.ImageCatalogGetImagesByNameAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.imagecatalog.ImageCatalogGetImagesFromDefaultCatalogAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.imagecatalog.ImageCatalogSetAsDefaultAction;
import com.sequenceiq.it.cloudbreak.newway.dto.imagecatalog.ImageCatalogTestDto;

@Service
public class ImageCatalogTestClient {

    public IntegrationTestAction<ImageCatalogTestDto> createV4() {
        return new ImageCatalogCreateAction();
    }

    public IntegrationTestAction<ImageCatalogTestDto> createIfNotExistV4() {
        return new ImageCatalogCreateIfNotExistsAction();
    }

    public IntegrationTestAction<ImageCatalogTestDto> createWithoutNameV4() {
        return new ImageCatalogCreateWithoutNameLoggingAction();
    }

    public IntegrationTestAction<ImageCatalogTestDto> deleteV4() {
        return new ImageCatalogDeleteAction();
    }

    public IntegrationTestAction<ImageCatalogTestDto> getV4() {
        return new ImageCatalogGetAction();
    }

    public IntegrationTestAction<ImageCatalogTestDto> getV4(Boolean withImages) {
        return new ImageCatalogGetAction(withImages);
    }

    public IntegrationTestAction<ImageCatalogTestDto> getImagesByNameV4() {
        return new ImageCatalogGetImagesByNameAction();
    }

    public IntegrationTestAction<ImageCatalogTestDto> getImagesByNameV4(String stackName) {
        return new ImageCatalogGetImagesByNameAction(stackName);
    }

    public IntegrationTestAction<ImageCatalogTestDto> getImagesFromDefaultCatalog() {
        return new ImageCatalogGetImagesFromDefaultCatalogAction();
    }

    public IntegrationTestAction<ImageCatalogTestDto> getImagesFromDefaultCatalog(CloudPlatform platform) {
        return new ImageCatalogGetImagesFromDefaultCatalogAction(platform);
    }

    public IntegrationTestAction<ImageCatalogTestDto> setAsDefault() {
        return new ImageCatalogSetAsDefaultAction();
    }

}
