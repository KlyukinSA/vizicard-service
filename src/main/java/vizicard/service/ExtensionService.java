package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.model.Extension;
import vizicard.repository.ExtensionRepository;

@Service
@RequiredArgsConstructor
public class ExtensionService {

    private final ExtensionRepository extensionRepository;

    public Extension getByName(String name) {
        Extension extension = extensionRepository.findByName(name.toUpperCase());
        if (extension == null) {
            return extensionRepository.findByName("OTHER");
        }
        return extension;
    }

}
