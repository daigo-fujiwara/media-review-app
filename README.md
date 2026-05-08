# MediaReviewApp

本や映画の「これから読みたい/観たいもの」と「読んだ/観た後の記録」を管理するWebアプリケーションです。

## 開発工程
1. 要件定義
2. 環境構築
   - Spring Initializrによるプロジェクト作成
   - GitHubとの連携
3. データモデル設計
   - Entityの作成
4. データ操作の実装
   - MediaRepository の作成
5. ビジネスロジックの実装
   - MediaService の作成
6. 窓口と画面の実装
   - MediaController
   - HTML/Thymeleaf
   - ブラッシュアップ
7. Renderでの公開

## 主な機能
- **WANTリスト**: 観たい映画や読みたい本を登録。
- **DONEリスト**: レビューと評価（5段階）を記入してアーカイブ。
- **クイックレビュー**: WANTリストを経由せず、いきなりレビューを投稿することも可能。

## 技術スタック
- Java 21
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Thymeleaf / Bootstrap 5

## 設定ファイル
- application.propertiesがRender（クラウド）用
- application-dev.propertiesがローカルPC用
- IntelliJで実行するときだけ、dev（自分用）の設定を使うように「実行構成の編集」をする。

## 作業予定
- 5/1週 : 環境構築
- 5/7週 : ひな形の作成
- 5/11週 : 説明とログイン機能の追加、テストコードの作成