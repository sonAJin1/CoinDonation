package com.example.sonaj.coindonation.CoinWallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonaj.coindonation.R;
import com.example.sonaj.coindonation.databinding.CoinTransferItemBinding;

import java.util.List;

public class CoinTransferAdapter extends RecyclerView.Adapter<CoinTransferAdapter.RViewHolder> {

    List<CoinTransferItem> itemCoinList;
    static Context context;

    public CoinTransferAdapter(Context context, List<CoinTransferItem> itemCoinList) {
        this.context = context;
        this.itemCoinList = itemCoinList;
    }

    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CoinTransferItemBinding binding = CoinTransferItemBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new RViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder rViewHolder, int i) {
        if (itemCoinList == null) return;
        final CoinTransferItem item = itemCoinList.get(i);
        rViewHolder.bind(item);
        final CoinTransferItemBinding binding = rViewHolder.binding;

        binding.tvTransferCoinName.setText(item.getCoinName());
        binding.tVTransferCoinBigSymbol.setText(item.getCoinBigSymbol());
        binding.tvTransferCoinSymbol.setText(item.getCoinSymbol());
        binding.tvTransferBalance.setText(item.getCoinBalance());
        binding.tvDate.setText(item.getDate());

        /** status 0: 전송완료
         *  status 1: 전송중
         *  status 2: 전송오류 */
        if(item.getTransferStatus().equals("0")){
            binding.tvTransferStatus.setText("전송완료");
            binding.rlCoinImage.setBackground(context.getDrawable(R.drawable.transfer_complete_circle));

        }else if(item.getTransferStatus().equals("1")){
            binding.tvTransferStatus.setText("전송중");
            binding.rlCoinImage.setBackground(context.getDrawable(R.drawable.transfer_ing_circle));
        }else{
            binding.tvTransferStatus.setText("전송실패");
            binding.rlCoinImage.setBackground(context.getDrawable(R.drawable.transfer_error_circle));
        }

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

    public void add(int index, CoinTransferItem item){
        itemCoinList.remove(index); //default 값 지우고 새로 추가
        itemCoinList.add(index,item);
        notifyDataSetChanged();
    }
    public void add(CoinTransferItem item){
        itemCoinList.add(item);
        notifyDataSetChanged();
    }
    public void update(int index, String transferStatus, String txHash, String date){
        CoinTransferItem item = itemCoinList.get(index);
        item.setTransferStatus(transferStatus);
        item.setTxHash(txHash);
        item.setDate(date);
        notifyDataSetChanged();
    }

    public class RViewHolder extends RecyclerView.ViewHolder {

        CoinTransferItemBinding binding;

        public RViewHolder(CoinTransferItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding = DataBindingUtil.bind(itemView);
        }

        void bind(CoinTransferItem item) {
            binding.setCoinKindsItem(item);
        }
    }
}
