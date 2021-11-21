package edu.wisc.ischool.wiscir.examples;

import edu.wisc.ischool.wiscir.utils.LuceneUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.Similarity;
import java.io.File;

/**
 * This is an changed example of accessing corpus statistics and corpus-level term statistics.
 *
 * @editor Ozan Koyuk  N20230337
 * @version 2021-11-21
 */
public class LuceneSearchExample {

	/*
	 * Olusturmus oldugum fonksiyon bir adet Similarity objesi almaktadir.
	 * Bu Similarity objesi, main fonksiyonumda olusturulan DFISimilarity objeleridir.
	 * Bu DFISimilarity objeleri sira ile bu fonksiyona parametre olarak gelmektedir.
	 */
	public static void search_with_param(Similarity similarity) {
		try {

            String pathIndex = LuceneBuildIndex.path_to_index;

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

            String field = "text"; // the field you hope to search for
            QueryParser parser = new QueryParser( field, analyzer ); // a query parser that transforms a text string into Lucene's query object

            String qstr = "query reformulation"; // this is the textual search query
            Query query = parser.parse( qstr ); // this is Lucene's query object

            // Okay, now let's open an index and search for documents
            Directory dir = FSDirectory.open( new File( pathIndex ).toPath() );
            IndexReader index = DirectoryReader.open( dir );

            // you need to create a Lucene searcher
            IndexSearcher searcher = new IndexSearcher( index );

            // make sure the similarity class you are using is consistent with those being used for indexing
            searcher.setSimilarity( similarity );

            int top = 10; // Let's just retrieve the talk 10 results
            TopDocs docs = searcher.search( query, top ); // retrieve the top 10 results; retrieved results are stored in TopDocs

            
            /*
             * Cikan skor degerlerini daha iyi kavrayabilmek icin, bu skor degerlerinin normalize edilmis hallerinide hesaplayip
             * ilgili konsol ciktisina eklemekteyim.
             */
            System.out.printf( "%-10s%-15s%-10s%-15s%s\n", "Rank", "DocNo", "Score", "Normalized", "Title" );
            int rank = 1;
            double _min = 999;
            double _max = -999.0;
            
            
            // Burada tum dokumanlar arasindaki minimum ve maksimum skor degerlerini buluyorum.
            for(ScoreDoc scoreDoc: docs.scoreDocs){
            	if(scoreDoc.score > _max)
            		_max = scoreDoc.score;
            	
            	if(scoreDoc.score < _min)
            		_min = scoreDoc.score;
            }
            
            for ( ScoreDoc scoreDoc : docs.scoreDocs ) {
            	// Explain fonksiyonunu kullanarak ilgili query ve dokumanin hesaplama ozetini kontrol ediyorum.
            	// System.out.println(searcher.explain(query, scoreDoc.doc));
                int docid = scoreDoc.doc;
                double score = scoreDoc.score;
                String docno = LuceneUtils.getDocno( index, "docno", docid );
                String title = LuceneUtils.getDocno( index, "title", docid );
                double normalized = (((score-_min)/(_max-_min)));
                System.out.printf( "%-10d%-15s%-10.4f%-15.4f%s\n", rank, docno, score, normalized, title );
                rank++;
            }
            index.close();
            dir.close();

        } catch ( Exception e ) {
            e.printStackTrace();
        }
	}
	
}
