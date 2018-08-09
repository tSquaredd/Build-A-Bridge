package bab.com.build_a_bridge


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.adapters.ConversationListAdapter
import kotlinx.android.synthetic.main.fragment_conversations.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Displays list of Conversations for the User
 */
class ConversationsFragment : Fragment() {

    lateinit var conversationAdapter: ConversationListAdapter
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.messages)

        //Adapter setup
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        conversations_rv.layoutManager = layoutManager
        conversations_rv.addItemDecoration(divider)
        conversations_rv.setHasFixedSize(true)
        conversationAdapter = ConversationListAdapter(context!!, activity as MainActivity)
        conversations_rv.adapter = conversationAdapter

        // Observe conversation data
        viewModel.conversationsLiveData.observe(this, Observer {

            it?.let { conversationList ->
                if (conversationList.isEmpty())
                    empty_conversations_list_tv.visibility = View.VISIBLE
                else {
                    conversationAdapter.setConversations(conversationList)
                    empty_conversations_list_tv.visibility = View.GONE
                }
            }
        })
    }
}
