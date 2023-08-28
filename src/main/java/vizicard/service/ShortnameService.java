package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.dto.DeviceDTO;
import vizicard.dto.ShortnameDTO;
import vizicard.model.Profile;
import vizicard.model.Shortname;
import vizicard.model.ShortnameType;
import vizicard.repository.ShortnameRepository;
import vizicard.utils.ProfileProvider;

@Service
@RequiredArgsConstructor
public class ShortnameService {

    private final ShortnameRepository shortnameRepository;
    private final ProfileProvider profileProvider;

    public DeviceDTO findByShortname(String shortname1) {
        Shortname shortname = shortnameRepository.findByShortname(shortname1);
        return new DeviceDTO(shortname.getId(), shortname.getOwner().getId(), shortname.getShortname());
    }

    public boolean add(ShortnameDTO dto) {
        if (null != shortnameRepository.findByShortname(dto.getShortname())) {
            return false;
        }
        Profile user = profileProvider.getUserFromAuth();
        if (dto.getType() == ShortnameType.MAIN) {
            Shortname oldMain = shortnameRepository.findByOwnerAndType(user, ShortnameType.MAIN);
            if (oldMain != null) {
                oldMain.setType(ShortnameType.USUAL);
                shortnameRepository.save(oldMain);
            }
        }
        shortnameRepository.save(new Shortname(user, dto.getShortname(), dto.getType()));
        return true;
    }

}
