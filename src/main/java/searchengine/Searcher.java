package searchengine;

import annotations.IndexDirectoryString;
import com.google.inject.Inject;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import utils.LuceneConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by EvanKing on 7/12/17.
 */
public class Searcher {

    IndexSearcher indexSearcher;
    QueryParser queryParser;

    @Inject
    public Searcher(@IndexDirectoryString String indexDirectoryString)
            throws IOException {
        Path indexDirectoryPath = Paths.get(indexDirectoryString);
        Directory indexDirectory = FSDirectory.open(indexDirectoryPath);
        IndexReader reader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(reader);
        queryParser = new QueryParser(LuceneConstants.DEFAULT_FIELD,
                new StandardAnalyzer());
    }

    public TopDocs search(String searchQuery)
            throws IOException, org.apache.lucene.queryparser.classic.ParseException {
        Query query = queryParser.parse(searchQuery);
        TopDocs topDocs = indexSearcher.search(query, LuceneConstants.MAX_NUMBER_OF_RESULTS);
        return topDocs;
    }


    public Document getDocument(ScoreDoc scoreDoc)
            throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

}
