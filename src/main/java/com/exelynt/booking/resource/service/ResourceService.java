package com.exelynt.booking.resource.service;

import com.exelynt.booking.resource.dto.ResourceRequestDTO;
import com.exelynt.booking.resource.dto.ResourceResponseDTO;

import java.util.List;

public interface ResourceService {

    ResourceResponseDTO createResource(ResourceRequestDTO request);

    List<ResourceResponseDTO> getAllResources();

    ResourceResponseDTO getResourceById(Long id);

    ResourceResponseDTO updateResource(Long id, ResourceRequestDTO request);

    void deleteResource(Long id);
}