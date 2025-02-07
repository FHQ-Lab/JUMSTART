import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplikasikotlin.R
import com.example.aplikasikotlin.User

class ChatListAdapter(private val userList: List<User>, private val onItemClick: (User) -> Unit) :
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return ChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    override fun getItemCount(): Int = userList.size

    class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        private val userName: TextView = itemView.findViewById(R.id.tvUserName)
        private val lastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)

        fun bind(user: User) {
            userName.text = user.fullname  // Langsung akses properti
            lastMessage.text = user.lastMessage  // Langsung akses properti

            Glide.with(itemView.context)
                .load(user.profileImage)  // Langsung akses properti
                .placeholder(R.drawable.ic_user_placeholder)
                .into(profileImage)
        }
    }
}
