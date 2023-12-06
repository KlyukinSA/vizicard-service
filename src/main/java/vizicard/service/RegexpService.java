package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.model.ContactEnum;
import vizicard.model.ContactType;

@Service
@RequiredArgsConstructor
public class RegexpService {

    public String getRegexpBy(ContactType contactType) {
        if (contactType.getType() == ContactEnum.PHONE) {
            return "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
        } else if (contactType.getType() == ContactEnum.MAIL) {
            return "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        } else {
            return "^" + contactType.getUrlBase() + "[-a-zA-Z0-9()@:%_+.~#?&/=]*$";
        }
    }

}
