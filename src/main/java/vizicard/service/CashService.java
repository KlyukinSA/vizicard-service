package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.model.Profile;
import vizicard.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class CashService {

	private final PrimaryService primaryService;
	private final ProfileRepository profileRepository;

	public boolean isPro(Profile profile) {
		return profileRepository.findById(primaryService.getPrimaryOrSelf(profile).getId()).get().getCash() > 0; // for UserDetailsService
	}

}
