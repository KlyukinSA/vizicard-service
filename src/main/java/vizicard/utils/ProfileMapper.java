package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import vizicard.dto.BriefResponseDTO;
import vizicard.model.Profile;
import vizicard.model.ShortnameType;
import vizicard.repository.ShortnameRepository;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ShortnameRepository shortnameRepository;

    private final ModelMapper modelMapper;

    public BriefResponseDTO mapBrief(Profile profile) {
        BriefResponseDTO res = modelMapper.map(profile, BriefResponseDTO.class);
        res.setMainShortname(getMainShortname(profile));
        return res;
    }

    public String getMainShortname(Profile profile) {
        return shortnameRepository.findByOwnerAndType(profile, ShortnameType.MAIN).getShortname();
    }

}
