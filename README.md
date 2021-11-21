# Hacettepe Üniversitesi
### VBM 681 Bilgi Erişim Sistemleri (Sonbahar 2021) Ara Sınavı


## Kaynak
* _[Projenin kaynağına buradan gidebilirsiniz.](https://github.com/jiepujiang/LuceneTutorial)_

## Amaç
* Verilen kaynak kodunu ve belirlediğimiz 3 benzerlik ölçütünü kullanarak belge bazlı skor puanları üretip gerekli çıkarım ve yorumlamaları yapmak.

## Kullanılan Benzerlik Ölçütleri
* Bu projede ```DFI Benzerlik ölçütü``` kullanılmıştır.
* İlgili ölçüte 3 farklı bağımsızlık modeli parametre verilerek test edilmiştir. Bu modeller ```Standardized, Saturated ve Chi-Squared``` modelleridir.

## Gereksinimler
* [Oracle JDK 11](https://www.oracle.com/tr/java/technologies/javase/jdk11-archive-downloads.html)
* [Lucene 8.10.1](http://archive.apache.org/dist/lucene/java/8.10.1/)

## Kullanılan IDE
* Eclipse IDE Version: 2021-09 (4.21.0)

## Kurulum
* Git clone işleminin ardından, ```src/main/java/edu/wisc/ischool/wiscir/examples/LuceneBuildIndex.java``` kodu açılır
* Kullanılacak corpus ve index için önceden belirlenmiş ```path_to_corpus``` ve ```path_to_index``` değişkenleri kontrol edilip güncellenir
* maven kütüphaneleri daha önceden kurulu ise kod çalıştırılır.
* Eğer maven kütüphaneleri daha önce kurulmamışsa, [Lucene 8.10.1](http://archive.apache.org/dist/lucene/java/8.10.1/) adresinden zip dosyası indirilir ve çıkarılır.
* Projeyi Eclipse IDE'sinden açtığımızda, "Project Explorer" bölümünde ```Maven Dependencies``` butonuna sağ tıklanarak ```Build Path -> Configure Build Path..``` butonuna tıklanır
* ```Class Path``` seçeneği seçilip, sağ kısımdan ```Add External JARs``` butonuna tıklanır.
* Açılan ekrandan, daha önce indirdiğimiz Lucene kaynak koduna gidilir ve şu kütüphaneler tek tek eklenir:
   * commons-compress-1.21.jar
   * lucene-core-8.10.1.jar
   * lucene-analyzers-common-8.10.1.jar
   * lucene-queryparser-8.10.1.jar
   * lucene-queries-8.10.1.jar
   * lucene-sandbox-8.10.1.jar
* Ardından ```Apply and Close``` diyerek kod tekrardan çalıştırılır.

## Çalıştırma
* Kodu çalıştırmak için tek gerekli işlem [Kurulum](https://github.com/ozankoyuk/LuceneExample#Kurulum) bölümünde açıklanan dosya yollarının güncellenmesidir.
* Kodda yapılan değişiklikler sonucu, herhangi bir veri girişi veya kod değişikliği olmadan tüm gerekli çıktılar ekrana bastırılmaktadır.

