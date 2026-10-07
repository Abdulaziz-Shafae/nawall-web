import React, {useState} from 'react';
import {api} from './api';
import {useApp,Button,Badge,Field,f,offerFields} from './ui';

export default function OfferComposer({options,after}) {
 const {t,toast,dialog}=useApp();
 const [values,setValues]=useState({skillId:'',description:'',mode:'ONLINE',tokenCost:1,capacity:1});
 const [busy,setBusy]=useState('');
 const [evaluation,setEvaluation]=useState(null);
 const [error,setError]=useState('');
 const fields=[f('skillId',{label:'Skills',options,required:true}),...offerFields.map(field=>field.name==='description'?{...field,hint:t('Include duration and topics so the AI can assess comparable offers.','أضف المدة والمواضيع ليتمكن الذكاء الاصطناعي من مقارنة العروض.')}:field)];
 const submit=async event=>{
  event.preventDefault();
  const action=event.nativeEvent.submitter?.value==='evaluate'?'evaluateOffer':'createOffer';
  setBusy(action);setError('');
  const {skillId,description,mode,tokenCost,capacity}=values;
  try {
   const result=await api(action,{skillId},{body:{description,mode,tokenCost,capacity}});
   if(action==='evaluateOffer')setEvaluation(result);
   else {toast(result?.message || t('Saved successfully'));dialog(null);await after?.();}
  } catch(err){setError(err.message);} finally{setBusy('');}
 };
 return <form onSubmit={submit}>
  <p>{t('Evaluation is optional and advisory. It never saves an offer or changes your price.','التقييم اختياري واستشاري. لا يحفظ العرض ولا يغير سعرك.')}</p>
  <fieldset disabled={Boolean(busy)} style={{border:0,padding:0,margin:0}}><div className="form-grid">{fields.map(field=><Field key={field.name} field={field} value={values[field.name]} onChange={value=>{setValues(current=>({...current,[field.name]:value}));setEvaluation(null);setError('');}}/>)}</div></fieldset>
  {error && <p className="form-error" role="alert">{error}</p>}
  {evaluation && <section className="panel" aria-live="polite" style={{marginTop:20}}>
   <h3>{t('AI price evaluation','تقييم السعر بالذكاء الاصطناعي')}</h3><Badge>{evaluation.verdict}</Badge>
   <p>{t('Proposed price','السعر المقترح')}: {evaluation.proposedTokens} {t('tokens')}</p>
   <p>{evaluation.suggestedTokens==null?t('There is not enough evidence to recommend a price.','لا توجد أدلة كافية للتوصية بسعر.'):`${t('Suggested price','السعر الموصى به')}: ${evaluation.suggestedTokens} ${t('tokens')}`}</p>
   <p>{evaluation.explanation}</p>{evaluation.suggestions?.length>0 && <ul>{evaluation.suggestions.map((suggestion,index)=><li key={index}>{suggestion}</li>)}</ul>}
  </section>}
  <div className="form-footer"><Button type="submit" name="action" value="evaluate" variant="secondary" busy={busy==='evaluateOffer'} disabled={Boolean(busy)}>{t('Evaluate price','تقييم السعر')}</Button><Button type="submit" name="action" value="save" busy={busy==='createOffer'} disabled={Boolean(busy)}>{t('Create offer')}</Button></div>
 </form>;
}
