package com.example.sonaj.coindonation.CoinWallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonaj.coindonation.R;
import com.example.sonaj.coindonation.databinding.CoinWalletItemBinding;

import java.util.List;

public class CoinKindAdapter extends RecyclerView.Adapter<CoinKindAdapter.RViewHolder> {

    List<CoinKindsItem> itemCoinList;
    static Context context;

    public CoinKindAdapter(Context context, List<CoinKindsItem> itemCoinList) {
        this.context = context;
        this.itemCoinList = itemCoinList;
    }

    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CoinWalletItemBinding binding = CoinWalletItemBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new CoinKindAdapter.RViewHolder(binding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RViewHolder rViewHolder, int i) {
        if (itemCoinList == null) return;
        final CoinKindsItem item = itemCoinList.get(i);
        rViewHolder.bind(item);
        final CoinWalletItemBinding binding = rViewHolder.binding;
        final String coinName = item.getCoinName();
        binding.tvCoinName.setText(coinName);
        binding.tvSymbolCoin.setText(item.getCoinSymbol());
        binding.tvBigSymbolCoin.setText(item.getCoinBigSymbol());
        binding.tvBalanceCoin.setText(item.getCoinBalance());

        if(i==1){ //2번째 아이템일 경우
            binding.tvCoinName.setTextColor(context.getColor(R.color.white));
            binding.tvSymbolCoin.setTextColor(context.getColor(R.color.white));
            binding.tvBigSymbolCoin.setTextColor(context.getColor(R.color.white));
            binding.tvBalanceCoin.setTextColor(context.getColor(R.color.white));
            binding.llWalletItem.setBackground(context.getDrawable(R.drawable.round_rect_main_color));
            binding.llWalletItem.setPadding(70,60,60,60);
            binding.viewDivider.setBackgroundColor(context.getColor(R.color.white));
            binding.ibTransfer.setBackgroundResource(R.drawable.circle_btn_white);
            binding.ibTransfer.setColorFilter(R.color.white);
        }

        // 토큰을 보내는 화면으로 넘어가게
        binding.ibTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), SendCoinActivity.class);
                intent.putExtra("type",coinName);
                if(coinName=="ETHEREUM"){
                    ((Activity) context).startActivityForResult(intent,0);
                }else{
                    ((Activity) context).startActivityForResult(intent,1);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (itemCoinList == null) return 0;
        return itemCoinList.size();
    }

    public void clear(){
        itemCoinList.clear();
        notifyDataSetChanged();
    }

    public void add(int index, CoinKindsItem item){
        itemCoinList.remove(index); //default 값 지우고 새로 추가
        itemCoinList.add(index,item);
        notifyDataSetChanged();
    }

    public class RViewHolder extends RecyclerView.ViewHolder {

        CoinWalletItemBinding binding;

        public RViewHolder(CoinWalletItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding = DataBindingUtil.bind(itemView);
        }

        void bind(CoinKindsItem item) {
            binding.setCoinKindsItem(item);
        }
    }
}
