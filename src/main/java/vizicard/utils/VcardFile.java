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
import vizicard.model.Contact;
import vizicard.model.ContactEnum;
import vizicard.model.Profile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Getter
public class VcardFile {

    private final byte[] bytes;
    private final String name;

    public VcardFile(Profile target) throws IOException {
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

    private VCard getVcard(Profile profile) throws IOException {
        VCard vcard = new VCard();
        if (isGoodForVcard(profile.getName())) {
            vcard.setFormattedName(profile.getName());
        }
        if (isGoodForVcard(profile.getTitle())) {
            vcard.addTitle(profile.getTitle());
        }
        if (isGoodForVcard(profile.getDescription())) {
            vcard.addNote(profile.getDescription());
        }
        if (profile.getCompany() != null && profile.getCompany().isStatus() &&
                isGoodForVcard(profile.getCompany().getName())) {
            vcard.setOrganization(profile.getCompany().getName());
        }
        if (isGoodForVcard(profile.getCity())) {
            Address address = new Address();
            address.setLocality(profile.getCity());
            vcard.addAddress(address);
        }

        int group = 0;
        for (Contact contact : profile.getContacts()) {
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

        if (profile.getAvatar() != null) {
            String url = profile.getAvatar().getUrl();
            InputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            Photo photo = new Photo(inputStream, ImageType.JPEG);
            vcard.addPhoto(photo); // TODO image types
        }

        return vcard;
    }

    private boolean isGoodForVcard(String string) {
        return string != null && string.length() > 0;
    }

    private String getVcardFileName(Profile target) {
        return target.getName() + ".vcf";
    }

}
