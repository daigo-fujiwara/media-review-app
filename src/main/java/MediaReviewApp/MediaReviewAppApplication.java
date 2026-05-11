package MediaReviewApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MediaReviewAppApplication {
	//Spring Bootを起動するmainメソッド
	public static void main(String[] args) {
		SpringApplication.run(MediaReviewAppApplication.class, args);
	}

	/*
	① サーバーの自動起動
	普通のJavaアプリと違い、Spring Bootは自分の中に Tomcat（Webサーバー） を内蔵している。外部のサーバーを借りるのではなく、自分自身をサーバーとして起動させる。

	② 設定の読み込み
	application.propertiesを読み込む。

	③ コンポーネント・スキャン
	アノテーションが付いたクラスを見つけ出す。
	・@Controller：リクエストを受ける窓口担当
	・@Service：ビジネスロジック担当
	・@Repository：データベースのインターフェイス

	④ DIコンテナへの登録と「注入」
	見つけたクラスを、Springが管理する 「DIコンテナ」 に登録する。

	⑤ 地図（マッピング）の作成
	各Controllerの中にある @GetMapping などを読み取り、「どのURLが来たらどのメソッドを呼ぶか」というマッピングをする。
	 */
}
