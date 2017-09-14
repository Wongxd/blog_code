// BookManager.aidl
package wang.wongxd.aidl;

// Declare any non-default types here with import statements
import wang.wongxd.aidl.bean.Book;

interface BookManager {

            List<Book> getBooks();

            void addBook(inout Book book);

}
