package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.dto.DeviceDTO;
import vizicard.dto.ShortnameDTO;
import vizicard.exception.CustomException;
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

    public Shortname create(String sn, ShortnameType type) {
        stopUsed(sn);
        Profile user = profileProvider.getUserFromAuth();
        if (type == ShortnameType.MAIN) {
            Shortname oldMain = shortnameRepository.findByOwnerAndType(user, ShortnameType.MAIN);
            if (oldMain != null) {
                oldMain.setType(ShortnameType.USUAL);
                shortnameRepository.save(oldMain);
            }
        }
        return shortnameRepository.save(new Shortname(user, sn, type));
    }

    public void stopUsed(String sn) {
        if (null != shortnameRepository.findByShortname(sn)) {
            throw new CustomException("shortname already in use", HttpStatus.FORBIDDEN);
        }
    }

    public String getMainShortname(Profile profile) {
        return shortnameRepository.findByOwnerAndType(profile, ShortnameType.MAIN).getShortname();
    }

}
