package vizicard.model;

//Создание таблицы devices, которая содержит поля
// id int not null с автогенерацией,
// owner_id int not null, ссылающееся на таблицу профилей и
// url varchar(8) not null. При этом url является уникальным.

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private Profile owner;

    @Column(length = 8, nullable = false, unique = true)
    private String url;

}
