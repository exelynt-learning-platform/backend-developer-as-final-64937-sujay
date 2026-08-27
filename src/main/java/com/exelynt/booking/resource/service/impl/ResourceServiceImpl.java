package com.exelynt.booking.resource.service.impl;

import com.exelynt.booking.resource.dto.ResourceRequestDTO;
import com.exelynt.booking.resource.dto.ResourceResponseDTO;
import com.exelynt.booking.resource.entity.Resource;
import com.exelynt.booking.resource.exception.ResourceNotFoundException;
import com.exelynt.booking.resource.repository.ResourceRepository;
import com.exelynt.booking.resource.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public ResourceResponseDTO createResource(ResourceRequestDTO request) {

        Resource resource = new Resource();

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setType(request.getType());
        resource.setPrice(request.getPrice());
        resource.setAvailable(request.getAvailable());

        Resource savedResource = resourceRepository.save(resource);

        return mapToResponse(savedResource);
    }

    @Override
    public List<ResourceResponseDTO> getAllResources() {

        return resourceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ResourceResponseDTO getResourceById(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found with id: " + id));

        return mapToResponse(resource);
    }

    @Override
    public ResourceResponseDTO updateResource(Long id, ResourceRequestDTO request) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found with id: " + id));

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setType(request.getType());
        resource.setPrice(request.getPrice());
        resource.setAvailable(request.getAvailable());

        Resource updatedResource = resourceRepository.save(resource);

        return mapToResponse(updatedResource);
    }

    @Override
    public void deleteResource(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found with id: " + id));

        resourceRepository.delete(resource);
    }

    private ResourceResponseDTO mapToResponse(Resource resource) {

        ResourceResponseDTO response = new ResourceResponseDTO();

        response.setId(resource.getId());
        response.setName(resource.getName());
        response.setDescription(resource.getDescription());
        response.setType(resource.getType());
        response.setPrice(resource.getPrice());
        response.setAvailable(resource.getAvailable());
        response.setCreatedAt(resource.getCreatedAt());
        response.setUpdatedAt(resource.getUpdatedAt());

        return response;
    }
}