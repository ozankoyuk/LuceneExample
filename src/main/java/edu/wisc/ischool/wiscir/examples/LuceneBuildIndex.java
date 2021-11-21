package edu.wisc.ischool.wiscir.examples;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.DFISimilarity;
import org.apache.lucene.search.similarities.IndependenceStandardized;
import org.apache.lucene.search.similarities.IndependenceSaturated;
import org.apache.lucene.search.similarities.IndependenceChiSquared;
import org.apache.lucene.search.similarities.Similarity;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * This is an changed example of building a Lucene index for the example corpus.
 *
 * @editor Ozan Koyuk  N20230337
 * @version 2021-11-21
 */

public class LuceneBuildIndex {
	// Her bir .java dosyasindan tek tek path degistirmek yerine,
	// sadece bu alttaki iki satiri degistirmek yeterlidir.
	// Diger dosyalarda buradaki degiskenleri kullanarak gerekli islemleri yapmaktadir.
	public static String path_to_corpus =  "D:/Eclipse/ws/LuceneTutorial/example_corpus.gz";
	public static String path_to_index = "D:/Eclipse/ws/LuceneTutorial/example_index_lucene";
	
	
	// Fonksiyon bir Similarity objesi almaktadir
	// Bu Similarity objesi, main fonksiyonda olusturulan DFISimilarity objesidir
	public static void generate_index_with_similarity(Similarity similarity) {
		try {

            // change the following input and output paths to your local ones
			String pathCorpus = path_to_corpus;
            String pathIndex = path_to_index;

            Directory dir = FSDirectory.open( new File( pathIndex ).toPath() );

            // Analyzer specifies options for text tokenization and normalization (e.g., stemming, stop words removal, case-folding)
            Analyzer analyzer = new Analyzer() {
                @Override
                protected TokenStreamComponents createComponents( String fieldName ) {
                    // Step 1: tokenization (Lucene's StandardTokenizer is suitable for most text retrieval occasions)
                    TokenStreamComponents ts = new TokenStreamComponents( new StandardTokenizer() );
                    // Step 2: transforming all tokens into lowercased ones (recommended for the majority of the problems)
                    ts = new TokenStreamComponents( ts.getSource(), new LowerCaseFilter( ts.getTokenStream() ) );
                    // Step 3: whether to remove stop words (unnecessary to remove stop words unless you can't afford the extra disk space)
                    // Uncomment the following line to remove stop words
                    // ts = new TokenStreamComponents( ts.getSource(), new StopFilter( ts.getTokenStream(), EnglishAnalyzer.ENGLISH_STOP_WORDS_SET ) );
                    // Step 4: whether to apply stemming
                    // Uncomment one of the following two lines to apply Krovetz or Porter stemmer (Krovetz is more common for IR research)
                    ts = new TokenStreamComponents( ts.getSource(), new KStemFilter( ts.getTokenStream() ) );
                    // ts = new TokenStreamComponents( ts.getSource(), new PorterStemFilter( ts.getTokenStream() ) );
                    return ts;
                }
            };

            IndexWriterConfig config = new IndexWriterConfig( analyzer );
            // Note that IndexWriterConfig.OpenMode.CREATE will override the original index in the folder
            config.setOpenMode( IndexWriterConfig.OpenMode.CREATE );

            
            // Burada main fonksiyonumdan gelen Similarity objesini set ediyorum.
            config.setSimilarity( similarity );

            IndexWriter ixwriter = new IndexWriter( dir, config );

            // This is the field setting for metadata field (no tokenization, searchable, and stored).
            FieldType fieldTypeMetadata = new FieldType();
            fieldTypeMetadata.setOmitNorms( true );
            fieldTypeMetadata.setIndexOptions( IndexOptions.DOCS );
            fieldTypeMetadata.setStored( true );
            fieldTypeMetadata.setTokenized( false );
            fieldTypeMetadata.freeze();

            // This is the field setting for normal text field (tokenized, searchable, store document vectors)
            FieldType fieldTypeText = new FieldType();
            fieldTypeText.setIndexOptions( IndexOptions.DOCS_AND_FREQS_AND_POSITIONS );
            fieldTypeText.setStoreTermVectors( true );
            fieldTypeText.setStoreTermVectorPositions( true );
            fieldTypeText.setTokenized( true );
            fieldTypeText.setStored( true );
            fieldTypeText.freeze();

            // You need to iteratively read each document from the example corpus file,
            // create a Document object for the parsed document, and add that
            // Document object by calling addDocument().

            // Well, the following only works for small text files. DO NOT follow this part for large dataset files.
            InputStream instream = new GZIPInputStream( new FileInputStream( pathCorpus ) );
            String corpusText = new String( IOUtils.toByteArray( instream ), "UTF-8" );
            instream.close();

            Pattern pattern = Pattern.compile(
                    "<DOC>.+?<DOCNO>(.+?)</DOCNO>.+?<TITLE>(.+?)</TITLE>.+?<AUTHOR>(.+?)</AUTHOR>.+?<SOURCE>(.+?)</SOURCE>.+?<TEXT>(.+?)</TEXT>.+?</DOC>",
                    Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL
            );

            Matcher matcher = pattern.matcher( corpusText );

            while ( matcher.find() ) {

                String docno = matcher.group( 1 ).trim();
                String title = matcher.group( 2 ).trim();
                String author = matcher.group( 3 ).trim();
                String source = matcher.group( 4 ).trim();
                String text = matcher.group( 5 ).trim();

                // Create a Document object
                Document d = new Document();
                // Add each field to the document with the appropriate field type options
                d.add( new Field( "docno", docno, fieldTypeMetadata ) );
                d.add( new Field( "title", title, fieldTypeText ) );
                d.add( new Field( "author", author, fieldTypeText ) );
                d.add( new Field( "source", source, fieldTypeText ) );
                d.add( new Field( "text", text, fieldTypeText ) );
                // Add the document to the index
                ixwriter.addDocument( d );
            }

            // remember to close both the index writer and the directory
            ixwriter.close();
            dir.close();

        } catch ( Exception e ) {
            e.printStackTrace();
        }
	}
	
    public static void main( String[] args ) {
    	/*
    	 * Kodda yapilacak degisikligi en aza indirmek amaciyla bu sekilde bir guncelleme yaptim
    	 * 
    	 * 1) Oncelikte 3 adet Independence objesini kullanararak DFISimilarity objelerini olusturuyorum.
    	 * 2) Ardindan olusturdugum ilk DFISimilarity objesini ve 'generate_index_with_similarity' fonksiyonunu kullanarak bir indeks olusturuyorum.
    	 * 3) Bu olusan indeksi 'search_with_param' ile kullanarak ilgili indeks icin bir sorgulama gerceklesitiriyorum
    	 * 4) Ardindan bu islem sonunda ekrana gerekli ciktilari bastirip islemi tamamliyorum.
    	 * 5) 4. adim tamamlandiktan sonra siradaki DFISimilarity objesi ile 2. adimdan devam ediyorum.
    	 * 
    	 * Bu sekilde kodda herhangi bir degisiklik yapmadan veya veri girisi olmadan, kolayca 3 model icin gerekli ciktilari bastirabiliyorum.
    	 */
    	
        DFISimilarity dfis_standard = new DFISimilarity(new IndependenceStandardized());
        DFISimilarity dfis_saturated = new DFISimilarity(new IndependenceSaturated());
        DFISimilarity dfis_chi = new DFISimilarity(new IndependenceChiSquared());

        System.out.println("DFISimilarity with Independence Standardized");
        generate_index_with_similarity(dfis_standard);
        LuceneSearchExample.search_with_param(dfis_standard);
        
        System.out.println("\nDFISimilarity with Independence Saturated");
        generate_index_with_similarity(dfis_saturated);
        LuceneSearchExample.search_with_param(dfis_saturated);

        System.out.println("\nDFISimilarity with Independence ChiSquared");
        generate_index_with_similarity(dfis_chi);
        LuceneSearchExample.search_with_param(dfis_chi);

        
    }

}
