package moxy.sample.dailypicture.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import coil.api.load
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import moxy.sample.R
import moxy.sample.dailypicture.domain.PictureOfTheDay
import moxy.sample.databinding.FragmentDailyPictureBinding
import moxy.sample.ui.ProgressRequestListener
import moxy.sample.ui.ViewBindingHolder
import moxy.sample.ui.openBrowser
import moxy.sample.ui.snackbar
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class DailyPictureFragment : MvpAppCompatFragment(),
    DailyPictureView {

    @Inject
    lateinit var presenterProvider: Provider<DailyPicturePresenter>

    // moxyPresenter delegate is the recommended way to create an instance of Presenter in Kotlin.
    // This is a factory for creating presenter for this fragment. You can do it
    // any way you want: manually, or with DI framework of your choice.
    // We use Dagger Hilt as an example of DI framework integration.
    private val presenter: DailyPicturePresenter by moxyPresenter { presenterProvider.get() }

    private val bindingHolder = ViewBindingHolder<FragmentDailyPictureBinding>()
    private val binding get() = bindingHolder.binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = bindingHolder.createView(viewLifecycleOwner) {
        FragmentDailyPictureBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshLayout.setOnRefreshListener { presenter.onRefresh() }
        binding.imageDailyPicture.setOnClickListener { presenter.onPictureClicked() }
        binding.buttonRandomize.setOnClickListener { presenter.onRandomizeClicked() }
    }

    override fun showPicture(picture: PictureOfTheDay) {
        binding.textTitle.text = picture.title
        if (picture.copyright.isEmpty()) {
            binding.textCopyright.isVisible = false
        } else {
            binding.textCopyright.isVisible = true
            binding.textCopyright.text = picture.copyright
        }
        binding.textPictureDescription.text = picture.explanation
        showImage(picture)
    }

    private fun showImage(picture: PictureOfTheDay) {
        when (picture.mediaType) {
            PictureOfTheDay.MediaType.IMAGE -> {
                binding.imageDailyPicture.isVisible = true
                binding.imageDailyPicture.load(picture.url) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder_image_padded)
                    error(R.drawable.ic_placeholder_error_padded)
                    listener(ProgressRequestListener { isProgress ->
                        binding.progressBar.isVisible = isProgress
                    })
                }
            }
            PictureOfTheDay.MediaType.VIDEO -> {
                binding.imageDailyPicture.isVisible = true
                binding.imageDailyPicture.setImageResource(R.drawable.ic_placeholder_video_padded)
            }
            PictureOfTheDay.MediaType.UNKNOWN -> {
                binding.imageDailyPicture.isVisible = false
            }
        }
    }

    override fun showProgress(isProgress: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = isProgress
    }

    override fun openBrowser(url: String) {
        openBrowser(Uri.parse(url)) {
            binding.root.snackbar(getString(R.string.common_error_browser_not_installed))
        }
    }

    override fun showError(message: String) {
        binding.root.snackbar(message)
    }
}