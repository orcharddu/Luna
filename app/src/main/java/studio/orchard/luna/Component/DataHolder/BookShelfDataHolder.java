package studio.orchard.luna.Component.DataHolder;

import android.content.Context;
import android.util.Log;

import studio.orchard.luna.Component.Constants;
import studio.orchard.luna.Component.IO;
import studio.orchard.luna.Component.SerializedClass.v0.BookShelf;

public class BookShelfDataHolder {

    private static volatile BookShelfDataHolder instance;
    private static BookShelf bookShelf;


    private BookShelfDataHolder(){ }

     public static BookShelfDataHolder getInstance(){
        if (instance == null) {
            synchronized (BookShelfDataHolder.class) {
                if (instance == null) {
                    instance = new BookShelfDataHolder();
                }
            }
        }
        return instance;
    }

    public BookShelf getBookShelf(){
        return bookShelf;
    }

    public BookShelf.BookSeries getBookSeries(int index){
        return bookShelf.bookSeriesList.get(index);
    }

    public void init(Context context){
        if (instance == null){
            getInstance();
        }else{
            IO.init(context);
            bookShelf = (BookShelf)IO.getSerializedData(context, Constants.Application.BOOKSHELF_FILE_NAME);
            if(bookShelf == null){
                IO.saveSerializedData(context, new BookShelf(), Constants.Application.BOOKSHELF_FILE_NAME);
                bookShelf = (BookShelf)IO.getSerializedData(context, Constants.Application.BOOKSHELF_FILE_NAME);
            }
        }
    }

    public String getBookShelfFileName(){
        return Constants.Application.BOOKSHELF_FILE_NAME;
    }

    public BookShelf.BookSeries newBookSeries(){
        BookShelf.BookSeries bookSeries = bookShelf.new BookSeries();
        bookSeries.index = -1;
        bookSeries.bookReadingProgress = -1;
        return bookSeries;
    }

    public BookShelf.Book newBook(){
        return bookShelf.new Book();
    }

    public int getPositionOnBookShelf(int bookSeriesID){
        for(int i = 0; i < bookShelf.bookSeriesList.size(); i++){
            if(bookShelf.bookSeriesList.get(i).bookSeriesID == bookSeriesID){
                return i;
            }
        }
        return -1;
    }

    public int getPositionOnBookShelf(String bookSeriesName){
        for(int i = 0; i < bookShelf.bookSeriesList.size(); i++){
            if(bookShelf.bookSeriesList.get(i).bookSeriesName.equals(bookSeriesName)){
                return i;
            }
        }
        return -1;
    }

    public void addBookSeriesToBookShelf(BookShelf.BookSeries bookSeries){
        bookSeries.index = bookShelf.bookSeriesList.size();
        bookShelf.bookSeriesList.add(bookSeries);
    }

    public void removeBookSeriesFromBookShelf(BookShelf.BookSeries bookSeries){
        removeBookSeriesFromBookShelf(bookSeries.index);
    }

    public void removeBookSeriesFromBookShelf(int bookSeriesIndex){
        //??????????????????index +1 ???????????????
        for(int i = bookSeriesIndex + 1; i < bookShelf.bookSeriesList.size(); i++){
            bookShelf.bookSeriesList.get(i).index--;
        }
        //??????????????????
        bookShelf.bookSeriesList.get(bookSeriesIndex).index = -1;
        bookShelf.readingProgress = -1;
        //????????????
        bookShelf.bookSeriesList.remove(bookSeriesIndex);
    }

    public void saveBookShelfToFile(Context context){
        IO.saveSerializedData(context, bookShelf, Constants.Application.BOOKSHELF_FILE_NAME);
    }


    public boolean mergeBookSeries(BookShelf.BookSeries source){
        boolean flag = false;
        int index = getPositionOnBookShelf(source.bookSeriesID);
        if(index == -1) { return false;}
        BookShelf.BookSeries bookSeries = bookShelf.bookSeriesList.get(index);
/*
        if(bookSeries.bookSeriesID != source.bookSeriesID) { bookSeries.bookSeriesID = source.bookSeriesID; flag = true;}
        if(!bookSeries.bookSeriesName.equals(source.bookSeriesName)) { bookSeries.bookSeriesName = source.bookSeriesName; flag = true;}
        if(!bookSeries.bookSeriesIntroduction.equals(source.bookSeriesIntroduction)) { bookSeries.bookSeriesIntroduction = source.bookSeriesIntroduction; flag = true;}
        if(!bookSeries.bookSeriesAuthor.equals(source.bookSeriesAuthor)) {  bookSeries.bookSeriesAuthor = source.bookSeriesAuthor; flag = true;}
        if(!bookSeries.bookSeriesAuthorAddress.equals(source.bookSeriesAuthorAddress)) { bookSeries.bookSeriesAuthorAddress = source.bookSeriesAuthorAddress; flag = true;}
        if(!bookSeries.bookSeriesStatus.equals(source.bookSeriesStatus)) { bookSeries.bookSeriesStatus = source.bookSeriesStatus; flag = true;}
        if(!bookSeries.bookSeriesUpdatedTime.equals(source.bookSeriesUpdatedTime)) { bookSeries.bookSeriesUpdatedTime = source.bookSeriesUpdatedTime; flag = true;}
        if(!bookSeries.bookSeriesLatestTitle.equals(source.bookSeriesLatestTitle)) { bookSeries.bookSeriesLatestTitle = source.bookSeriesLatestTitle; flag = true;}
        if(!bookSeries.bookSeriesAddress.equals(source.bookSeriesAddress)) { bookSeries.bookSeriesAddress = source.bookSeriesAddress; flag = true;}
        if(!bookSeries.bookSeriesCoverAddress.equals(source.bookSeriesCoverAddress)) { bookSeries.bookSeriesCoverAddress = source.bookSeriesAddress; flag = true;}
        //if(!bookSeries.bookSeriesCover.equals(source.bookSeriesCover)){bookSeries.bookSeriesCover = source.bookSeriesCover; flag = true;}
        if(bookSeries.bookSeriesTag.size() != source.bookSeriesTag.size()) { bookSeries.bookSeriesTag = source.bookSeriesTag; flag = true;}
        if(bookSeries.bookSeriesTagAddress.size() != source.bookSeriesTagAddress.size()) {bookSeries.bookSeriesTagAddress = source.bookSeriesTagAddress; flag = true;}
*/

        //??????bookList????????????????????????????????????????????????
        if(bookSeries.bookList.size() > source.bookList.size()) return false;
        //????????????bookList???chapterList???????????????
        for(int i = 0; i < bookSeries.bookList.size(); i++){
            BookShelf.Book book = bookSeries.bookList.get(i);
            BookShelf.Book bookSource = source.bookList.get(i);
            if(book.chapterTitle.size() < bookSource.chapterTitle.size()){
                //??????chapterList?????????????????????????????????chapterList???Address????????????????????????????????????
                flag = true;
                book.isLocalized = 0;
                for(int j = book.chapterTitle.size(); j < bookSource.chapterTitle.size(); j++){
                    book.chapterTitle.add(bookSource.chapterTitle.get(j));
                    book.chapterAddress.add(bookSource.chapterAddress.get(j));
                    book.chapterReadingProgress.add(bookSource.chapterReadingProgress.get(j));
                    Log.d("TAG_", "aabbcc"+bookSource.illustrationNum.get(j));

                    book.illustrationNum.add(bookSource.illustrationNum.get(j));
                }
            }
        }
        //??????bookList???????????????
        if(bookSeries.bookList.size() < source.bookList.size()){
            flag = true;
            for(int i = bookSeries.bookList.size(); i < source.bookList.size(); i++){
                bookSeries.bookList.add(source.bookList.get(i));
            }
        }
        //??????????????????????????????????????????true?????????????????????
        return flag || bookSeries.updateNotification;
    }
}
