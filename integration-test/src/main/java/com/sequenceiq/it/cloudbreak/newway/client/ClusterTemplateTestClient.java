package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.clustertemplate.ClusterTemplateCreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.clustertemplate.ClusterTemplateDeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.clustertemplate.ClusterTemplateGetAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.clustertemplate.ClusterTemplateListAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.clustertemplate.DeleteClusterFromClusterTemplateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.clustertemplate.LaunchClusterFromClusterTemplateAction;
import com.sequenceiq.it.cloudbreak.newway.dto.clustertemplate.ClusterTemplateTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.stack.StackTemplateTestDto;

@Service
public class ClusterTemplateTestClient {

    public IntegrationTestAction<ClusterTemplateTestDto> createV4() {
        return new ClusterTemplateCreateAction();
    }

    public IntegrationTestAction<ClusterTemplateTestDto> getV4() {
        return new ClusterTemplateGetAction();
    }

    public IntegrationTestAction<ClusterTemplateTestDto> listV4() {
        return new ClusterTemplateListAction();
    }

    public IntegrationTestAction<ClusterTemplateTestDto> deleteV4() {
        return new ClusterTemplateDeleteAction();
    }

    public IntegrationTestAction<ClusterTemplateTestDto> deleteCluster(String stackTemplateKey) {
        return new DeleteClusterFromClusterTemplateAction(stackTemplateKey);
    }

    public IntegrationTestAction<ClusterTemplateTestDto> deleteCluster(Class<StackTemplateTestDto> stackTemplateKey) {
        return new DeleteClusterFromClusterTemplateAction(stackTemplateKey);
    }

    public IntegrationTestAction<ClusterTemplateTestDto> launchCluster(String stackTemplateKey) {
        return new LaunchClusterFromClusterTemplateAction(stackTemplateKey);
    }

    public IntegrationTestAction<ClusterTemplateTestDto> launchCluster(Class<StackTemplateTestDto> stackTemplateKey) {
        return new LaunchClusterFromClusterTemplateAction(stackTemplateKey);
    }

}