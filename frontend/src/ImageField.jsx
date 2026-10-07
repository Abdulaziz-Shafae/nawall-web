import React,{useEffect,useState} from 'react';
import {useApp,Button} from './ui';
import {safeImageUrl} from './rules';

export default function ImageField({field,value,onChange,id}) {
 const {t}=useApp();const [preview,setPreview]=useState('');const [error,setError]=useState('');
 useEffect(()=>{if(value instanceof File){const url=URL.createObjectURL(value);setPreview(url);return ()=>URL.revokeObjectURL(url);}setPreview(safeImageUrl(value)||'');},[value]);
 return <div className="field image-field"><label htmlFor={id}><span>{t(field.label)}</span><input id={id} name={field.name} type="file" accept="image/jpeg,image/png,.jpg,.jpeg,.png" disabled={field.disabled} onChange={event=>{
  const file=event.target.files?.[0];if(!file)return;
  if(!['image/jpeg','image/png'].includes(file.type)||file.size>2*1024*1024){setError(t('Choose a JPG or PNG image no larger than 2 MB','اختر صورة JPG أو PNG لا يزيد حجمها عن ٢ ميغابايت'));event.target.setCustomValidity(t('Choose a valid image','اختر صورة صالحة'));event.target.value='';return;}
  event.target.setCustomValidity('');setError('');onChange(file);
 }}/></label><small>{t('JPG or PNG · up to 2 MB','JPG أو PNG · حتى ٢ ميغابايت')}</small>{preview && <div className="image-preview"><img src={preview} alt={t(field.label)}/><Button type="button" variant="text" onClick={()=>{onChange('');setError('');const input=document.getElementById(id);if(input){input.value='';input.setCustomValidity('');}}}>{t('Remove image','إزالة الصورة')}</Button></div>}{error && <p className="form-error" role="alert">{error}</p>}</div>;
}
