package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vizicard.exception.CustomException;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.repository.CardRepository;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class ProfileProvider {

    private final CardRepository cardRepository;

    public Account getUserFromAuth() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (Account) request.getAttribute("user");
    }

    public Card getTarget(Integer id) {
        CustomException exception = new CustomException("The card doesn't exist", HttpStatus.NOT_FOUND);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> exception);
        if (!card.isStatus()) {
            throw exception;
        }
        return card;
//        return cardRepository.findById(id).filter(Card::isStatus).get();
    }

}
