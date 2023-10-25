package vizicard.service;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import ezvcard.parameter.ImageType;
import ezvcard.property.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vizicard.model.Contact;
import vizicard.model.ContactEnum;
import vizicard.model.Card;
import vizicard.service.CloudFileService;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class VcardFileService {

    private final CloudFileService cloudFileService;

    @Value("${front-url-base}")
    private String urlBase;

    @SneakyThrows
    public byte[] getText(Card target) {
        return getVcardBytes(getVcard(target));
    }

    public String getName(Card target) {
        return getVcardFileName(target);
    }

    private byte[] getVcardBytes(VCard vcard) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        VCardWriter writer = new VCardWriter(outputStream, VCardVersion.V3_0);
        writer.getVObjectWriter().getFoldedLineWriter().setLineLength(null);
        writer.setAddProdId(false);
        writer.write(vcard);
        writer.close();

        return outputStream.toByteArray();
    }

    private VCard getVcard(Card card) throws IOException {
        VCard vcard = new VCard();
        if (isGoodForVcard(card.getName())) {
            vcard.setFormattedName(card.getName());
        }
        if (isGoodForVcard(card.getTitle())) {
            vcard.addTitle(card.getTitle());
        }
        if (isGoodForVcard(card.getDescription())) {
            vcard.addNote(card.getDescription());
        }
        if (card.getCompany() != null && card.getCompany().isStatus() &&
                isGoodForVcard(card.getCompany().getName())) {
            vcard.setOrganization(card.getCompany().getName());
        }
        if (isGoodForVcard(card.getCity())) {
            Address address = new Address();
            address.setLocality(card.getCity());
            vcard.addAddress(address);
        }
        vcard.setProductId(urlBase);

        int group = 1;
        addGroupedLink(group, vcard, "CARD-SOURCE", urlBase);
        for (Contact contact : card.getContacts()) {
            ContactEnum contactEnum = contact.getType().getType();
            String string = contact.getContact();
            if (isGoodForVcard(string)) {
                if (contactEnum == ContactEnum.PHONE) {
                    vcard.addTelephoneNumber(string);
                } else if (contactEnum == ContactEnum.MAIL) {
                    vcard.addEmail(string);
                } else if (contactEnum == ContactEnum.SITE) {
                    vcard.addUrl(string);
                } else {
                    group++;
                    addGroupedLink(group, vcard, contactEnum.toString(), string);
                }
            }
        }

        if (card.getAvatarId() != null) {
            String url = cloudFileService.findById(card.getAvatarId()).getUrl();
            InputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            Photo photo = new Photo(inputStream, ImageType.JPEG);
            vcard.addPhoto(photo); // TODO image types
        }

        return vcard;
    }

    private void addGroupedLink(int group, VCard vcard, String type, String target) {
        String groupName = "item" + group;
        RawProperty property = vcard.addExtendedProperty("X-ABLABEL", type);
        property.setGroup(groupName);
        Url url = vcard.addUrl(target);
        url.setGroup(groupName);
    }

    private boolean isGoodForVcard(String string) {
        return string != null && string.length() > 0;
    }

    private String getVcardFileName(Card target) {
        return target.getName() + ".vcf";
    }

}
