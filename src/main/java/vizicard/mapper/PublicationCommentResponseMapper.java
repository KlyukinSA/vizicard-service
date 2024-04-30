package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.publication.CommentResponse;
import vizicard.dto.publication.PublicationResponse;
import vizicard.model.Card;
import vizicard.model.Publication;
import vizicard.service.PublicationService;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PublicationCommentResponseMapper {

    private final ModelMapper modelMapper;
    private final PublicationService publicationService;

    public List<? extends PublicationResponse> getResponse(List<Publication> list, Function<Publication, Card> targetProvider, Class responseClass) {
        return list.stream()
                .filter(publicationService::isComment)
                .map(e -> {
                    PublicationResponse dto = (PublicationResponse) modelMapper.map(e, responseClass);
//                    dto.setProfile(cardMapper.mapToBrief(targetProvider.apply(e)));
                    return dto;})
                .collect(Collectors.toList());
    }

}
