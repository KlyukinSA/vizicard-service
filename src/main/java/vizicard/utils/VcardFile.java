package vizicard.utils;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import ezvcard.parameter.ImageType;
import ezvcard.property.Address;
import ezvcard.property.Photo;
import ezvcard.property.RawProperty;
import ezvcard.property.Url;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import vizicard.model.Contact;
import vizicard.model.ContactEnum;
import vizicard.model.Card;
import vizicard.repository.CloudFileRepository;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Getter
public class VcardFile {

    @Autowired
    private CloudFileRepository cloudFileRepository;

    private final byte[] bytes;
    private final String name;

    public VcardFile(Card target) throws IOException {
        this.bytes = getVcardBytes(getVcard(target));
        this.name = getVcardFileName(target);
    }

    private byte[] getVcardBytes(VCard vcard) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        VCardWriter writer = new VCardWriter(outputStream, VCardVersion.V3_0);
        writer.getVObjectWriter().getFoldedLineWriter().setLineLength(null);
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

        int group = 0;
//        Function f = (type)
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
                    String groupName = "item" + group;
                    String type = contactEnum.toString();
                    RawProperty property = vcard.addExtendedProperty("X-ABLABEL", type);
                    property.setGroup(groupName);
                    Url url = vcard.addUrl(string);
                    url.setGroup(groupName);
                }
            }
        }

        if (card.getAvatarId() != null) {
            String url = cloudFileRepository.findById(card.getAvatarId()).get().getUrl();
            InputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            Photo photo = new Photo(inputStream, ImageType.JPEG);
            vcard.addPhoto(photo); // TODO image types
        }

        return vcard;
    }

    private boolean isGoodForVcard(String string) {
        return string != null && string.length() > 0;
    }

    private String getVcardFileName(Card target) {
        return target.getName() + ".vcf";
    }

}
