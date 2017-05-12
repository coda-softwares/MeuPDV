package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by dovahkiin on 28/04/17.
 */

public abstract class HandlerPadrao implements Transaction.Handler {
    Runnable after;

    public HandlerPadrao(Runnable r) {
        after = r;
    }

    @Override
    public abstract Transaction.Result doTransaction(MutableData mutableData);

    @Override
    public void onComplete(DatabaseError dbError, boolean b, DataSnapshot dataSnapshot) {
        if (dbError != null) {
            //TODO: Atualizar esse metodo quando novas permissões forem implementadas
            FirebaseCrash.report(dbError.toException());
            System.exit(1);
        }
        if (after != null) {
            after.run();
        }
    }
}
