package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.tab.TabDTO;
import vizicard.dto.tab.TabReorderDTO;
import vizicard.dto.tab.TabTypeDTO;
import vizicard.exception.CustomException;
import vizicard.model.Card;
import vizicard.model.Tab;
import vizicard.model.TabType;
import vizicard.model.TabTypeEnum;
import vizicard.repository.TabRepository;
import vizicard.repository.TabTypeRepository;
import vizicard.utils.ProfileProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tabs")
@RequiredArgsConstructor
public class TabController {

    private final TabRepository tabRepository;
    private final TabTypeRepository tabTypeRepository;
    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    @GetMapping("types")
    public List<TabTypeDTO> getAllTypes() {
        return tabTypeRepository.findAll().stream()
                .map(t -> modelMapper.map(t, TabTypeDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("my")
    @PreAuthorize("isAuthenticated()")
    public List<TabDTO> getOfCurrentCard() {
        Card card = profileProvider.getUserFromAuth().getCurrentCard();
        precreateTabs(card);
        return tabRepository.findAllByCardOwner(card).stream()
                .map(t -> modelMapper.map(t, TabDTO.class))
                .collect(Collectors.toList());
    }

    private void precreateTabs(Card card) {
        if (tabRepository.findAllByCardOwner(card).isEmpty()) {
            List<TabType> types = tabTypeRepository.findAll();
            int i = 0;
            for (TabType type : types) {
                tabRepository.save(new Tab(type, card, false, i));
                i++;
            }
        }
    }

    @PutMapping("{type}/set-hidden")
    public TabDTO setTabIsHidden(@PathVariable TabTypeEnum type, boolean hidden) {
        Card card = profileProvider.getUserFromAuth().getCurrentCard();
        precreateTabs(card);
        Tab tab = tabRepository.findByTypeTypeAndCardOwner(type, card).get();
        tab.setHidden(hidden);
        tabRepository.save(tab);
        return modelMapper.map(tab, TabDTO.class);
    }

    @PutMapping("order")
    @PreAuthorize("isAuthenticated()")
    public List<TabDTO> reorder(@RequestBody List<TabReorderDTO> dto) {
        List<TabTypeEnum> ids = dto.stream().map(TabReorderDTO::getType).collect(Collectors.toList());
        List<Integer> orders = dto.stream().map(TabReorderDTO::getOrder).collect(Collectors.toList());
        return reorderTabs(ids, orders).stream()
                .map(t -> modelMapper.map(t, TabDTO.class))
                .collect(Collectors.toList());
    }

    private List<Tab> reorderTabs(List<TabTypeEnum> ids, List<Integer> orders) { // TODO same as CardService::reorder
        Card card = profileProvider.getUserFromAuth().getCurrentCard();
        precreateTabs(card);
        Set<Integer> currents = ids.stream().map(id -> tabRepository.findByTypeTypeAndCardOwner(id, card).get().getOrder()).collect(Collectors.toSet());
        Set<Integer> news = new HashSet<>(orders);
        if (!Objects.equals(currents, news)) {
            throw new CustomException("sets of orders must be equal", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        for (int i = 0; i < ids.size(); i++) {
            Tab tab = tabRepository.findByTypeTypeAndCardOwner(ids.get(i), card).get();
            Integer order = orders.get(i);
            if (!Objects.equals(tab.getOrder(), order)) {
                Tab conflict = tabRepository.findByCardOwnerAndOrder(card, order);
                conflict.setOrder(0);
                tabRepository.save(conflict);

                int temp = tab.getOrder();
                tab.setOrder(order);
                tabRepository.save(tab);

                conflict.setOrder(temp);
                tabRepository.save(conflict);
            }
        }
        return tabRepository.findAllByCardOwner(card);
    }

}
