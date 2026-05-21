# MediaReviewApp

メディアコンテンツの「これから読みたい/観たいもの」と「読んだ/観た後の記録」を管理するWebアプリケーションです。

## 開発工程
1. 要件定義
2. 環境構築（IntelliJ/GitHub）
3. データモデル設計（Entity）
4. データ操作の実装（Repository）
5. ビジネスロジックの実装（Service）
6. 窓口の実装（Controller）
7. 画面の実装（HTML/Thymeleaf）
8. エラー処理の実装
9. テストコード作成と検証
10. Renderでの公開
11. ブラッシュアップ 

※ 新しい機能を追加するたびに「3. 設計」から「11. ブラッシュアップ」までの工程を繰り返し実施する。

## 主な機能
- **レビュー一覧表示**: 今まで登録したレビューを一覧で表示する機能。
- **レビュー登録/削除**: レビューを登録/削除する機能
- **外部API連携**: コンテンツの画像をAPIで取得する機能

## 今後追加するかもしれない機能
- ユーザー認証
- 検索・絞り込み機能
- 並び替え機能
- お気に入りタグ
- 統計ダッシュボード

## 技術スタック
- Java 21
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Thymeleaf / Bootstrap 5

## 設定ファイル
- application.propertiesがRender（クラウド）用
- application-dev.propertiesがローカルPC用

※ローカルPCで実行する場合、devの設定を使うように「実行構成の編集」をする。

## 外部API連携
1. Google Books API
   - 用途: ジャンル BOOK の書籍画像（サムネイル）取得 
   - 認証: APIキー方式 
   - 環境変数: GOOGLE_BOOKS_API_KEY
   - ドキュメント: [Google Books APIs](https://developers.google.com/books)
2. TMDB (The Movie Database) API
   - 用途: ジャンル MOVIE および DRAMA のポスター画像取得 
   - 認証: APIキー方式 (v3)
   - 環境変数: TMDB_API_KEY
   - ドキュメント: [TMDB API Documentation](https://developer.themoviedb.org/docs/getting-started)
3. iTunes API
   - 用途: 指定したキーワード（曲名、アーティスト名、アルバム名）に関連するジャケット画像（アートワーク）、視聴URL、メタデータを取得
   - 認証: 不要
   - 環境変数: 不要
   - ドキュメント: [iTunes Search API 公式ドキュメント](https://performance-partners.apple.com/search-api)
4. RAWG API
   - 用途: ジャンル GAME のビジュアル画像（背景画像/background_image）の取得
   - 認証: APIキー方式
   - 環境変数: RAWG_API_KEY
   - ドキュメント: [RAWG API Documentation](https://rawg.io/apidocs)

## 作業予定
- 5/1週 : 環境構築
  - 5/1 : IntelliJインストール、GitHubアカウント作成、要件定義
- 5/7週 : ひな形の作成
  - 5/7 : 要件定義、 Spring Initializr
  - 5/8 : ひな形作成、Renderで公開
- 5/11週 : 説明と外部API連携機能の追加、テストコードの作成
  - 5/11 : Gitの練習、説明の追加、エラー処理追加
  - 5/12 : 説明追加、外部API追加
  - 5/13 : 予測候補機能追加
  - 5/14 : 説明追加、テストコードの作成
  - 5/15 : 説明追加、内容理解
- 5/18週 : 説明追加、内容理解
  - 5/18 : 説明追加、内容理解
  - 5/19 : ウェルカムポップアップの追加
  - 5/20 : index.htmlを各要素に分類
  - 5/21 : API連携の箇所の修正