package Service;

import DAO.CMRelationDao;
import DTO.CMRelation;
import Entity.CollegeMajor;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaho on 2017/3/10.
 */
public class RelationService {

    private Session session = null;

    public RelationService(Session session){
        this.session = session;
    }

    public void insertRelations(List<CMRelation> cmRelations){

        CMRelationDao clgMjrDao = new CMRelationDao(session);
        for(CMRelation cmRelation :cmRelations){
            CollegeMajor collegeMajor = new CollegeMajor();
            collegeMajor.setClgName(cmRelation.getClgName());
            collegeMajor.setMjrName(cmRelation.getMjrName());
            collegeMajor.setMjrLevel(cmRelation.getMjrLevel());
            clgMjrDao.insert(collegeMajor);
        }
    }

    public List<CMRelation> getRelations(){

        CMRelationDao clgMjrDao = new CMRelationDao(session);
        List<CollegeMajor> clgMjrs = clgMjrDao.getRelations();
        List<CMRelation> cmRelations = new ArrayList<CMRelation>();

        for (CollegeMajor cm:clgMjrs)
            cmRelations.add(this.convert(cm));

        return cmRelations;
    }



    private CMRelation convert(CollegeMajor cm){

        CMRelation cmRelation = new CMRelation();
        cmRelation.setClgName(cm.getClgName());
        cmRelation.setMjrName(cm.getMjrName());
        cmRelation.setMjrLevel(cm.getMjrLevel());

        return cmRelation;
    }


}
