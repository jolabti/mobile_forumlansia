package Model;

public class PostModel {
    private String post_id;
    private String posting;
    private String waktu;
    private String email;


    public PostModel(String pos_id, String posting, String waktu, String email){

        this.post_id = pos_id;
        this.posting = posting;
        this.waktu = waktu;
        this.email = email;

    }

    public String getPos_id() {
        return post_id;
    }

    public void setPos_id(String pos_id) {
        this.post_id = pos_id;
    }

    public String getPosting() {
        return posting;
    }

    public void setPosting(String posting) {
        this.posting = posting;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }





    /*  "post_id": "2",
            "posting": "Siapa yang tak kenal dengan internet dan media sosial? Setiap orang umumnya tidaklah asing dengan dua hal ini. Tak tua ataupun muda seringkali memainkannya dan bahkan anak-anak yang baru beranjak dewa",
            "waktu": "2018-07-17 10:03:27",
            "id_user": "1",
            "urlgambar": "",
            "user_id": "1",
            "email": "admin@admin.com",
            "password": "f6fdffe48c908deb0f4c3bd36c032e72",
            "name": "Admin",
            "kota": null,
            "role": "admin",
            "status": "1",
            "created_at": "2017-01-12 12:07:57",
            "updated_at": "2017-01-12 05:07:59"*/
}
