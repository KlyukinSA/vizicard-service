package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.CloudFileDTO;
import vizicard.model.CloudFile;
import vizicard.model.Extension;

@Component
@RequiredArgsConstructor
public class CloudFileMapper {

    private final ModelMapper modelMapper;

    public CloudFileDTO mapToDTO(CloudFile cloudFile) {
        Extension extension = cloudFile.getExtension();
        cloudFile.setExtension(null);
        CloudFileDTO res = modelMapper.map(cloudFile, CloudFileDTO.class);
        res.setExtension(extension.getName());
        res.setColor(extension.getColor());
        cloudFile.setExtension(extension);
        return res;
    }

}