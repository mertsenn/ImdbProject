package com.mertsen.imdbproject.service

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mertsen.imdbproject.model.MovieResponse
import com.mertsen.imdbproject.model.Moviess
import retrofit2.HttpException
import java.io.IOException

private const val TMDB_STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 20

// PagingSource, sayfa tabanlı veri akışı sağlamak için kullanılan bir sınıftır
class MoviesPagingSource(private val service: MovieService): PagingSource<Int, Moviess>() {

    // Verileri yüklemek için kullanılan yöntem
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Moviess> {
        val pageIndex = params.key ?: TMDB_STARTING_PAGE_INDEX // Yüklenen sayfa numarası veya başlangıç sayfa numarası

        return try {
            // Servis çağrısı ile bir sonraki sayfa verileri alınır
            val response = service.getNextMoviePage(pageNum = pageIndex)
            val movies = response.results
            val nextKey = if (movies.isEmpty()) {
                null // Eğer sonraki sayfada veri yoksa nextKey null olur
            } else {
                pageIndex + (params.loadSize / NETWORK_PAGE_SIZE) // Sonraki sayfa numarası hesaplanır
            }

            // LoadResult.Page ile veriler, önceki sayfa numarası ve sonraki sayfa numarası döndürülür
            LoadResult.Page(
                data = movies,
                prevKey = if (pageIndex == TMDB_STARTING_PAGE_INDEX) null else pageIndex,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            // IO hatası durumunda LoadResult.Error ile hata döndürülür
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            // HTTP istemcisi hatası durumunda LoadResult.Error ile hata döndürülür
            LoadResult.Error(exception)
        }
    }

    // Sayfa yenilenmesi için kullanılan anahtarın hesaplanması
    override fun getRefreshKey(state: PagingState<Int, Moviess>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // En yakın sayfa numarası alınır
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
