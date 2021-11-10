forwarding_protocol = Proto("forwarding_protocol",  "ForwardingProtocol")

packet_type = ProtoField.uint8("forwarding_protocol.packet_type", "PacketType", base.DEC)
type_lv = ProtoField.uint8("forwarding_protocol.type_lv", "Type", base.DEC)
t_length_v = ProtoField.uint8("forwarding_protocol.t_length_v", "Length", base.DEC)
tl_value = ProtoField.string("forwarding_protocol.tl_value"     , "Value"    , base.ASCII)
payload_length = ProtoField.uint8("forwarding_protocol.payload_length", "Payload Length", base.DEC)
payload_content = ProtoField.string("forwarding_protocol.payload_content"     , "Payload"    , base.ASCII)

forwarding_protocol.fields = { packet_type, type_lv, t_length_v, tl_value, payload_length, payload_content }


function get_packet_name(type)
  local type_name = "Unknown"
      if type ==    1 then type_name = "ROUTER"
  elseif type ==    2 then type_name = "ENDPOINT"
  elseif type ==    3 then type_name = "ACK"
  elseif type ==    4 then type_name = "CONTROLLER" end
  return type_name
end

function get_type(type)
  local type_status = "Unknown"
      if type ==    1 then type_status = "NET ID"
  elseif type ==    2 then type_status = "COMBINATION" end
  return type_status
end

function forwarding_protocol.dissector(buffer, pinfo, tree)
  length = buffer:len()
  if length == 0 then return end

  pinfo.cols.protocol = forwarding_protocol.name

  local subtree = tree:add(forwarding_protocol, buffer(), "My Protocol Data")

  local type = buffer(0,1):le_uint()
  local type_name = get_packet_name(type)

  subtree:add_le(packet_type, buffer(0,1)):append_text(" (" .. type_name .. ")")

  if type == 3 then
    return 
  end

  -- start TLV
  local type_one = buffer(1,1):le_uint()
  local type_one_str = get_type(type_one)
  subtree:add_le(type_lv, type_one):append_text(" (" .. type_one_str .. ")")
  
  local number_of_lvs = 0
  local current_pos = 0
  if type_one == 1 then
    number_of_lvs = 1
    current_pos = 2
  else
    number_of_lvs = buffer(2,1):le_uint()
    current_pos = 3
  end
  local current_substree_number = 1
  while( number_of_lvs > 0 )
  do
    local subtree_local = subtree:add(forwarding_protocol, buffer(), "TLV"..current_substree_number)
    local length_local = buffer(current_pos,1):le_uint()
    subtree_local:add_le(t_length_v,length_local)
    current_pos = current_pos + 1
    subtree_local:add_le(tl_value, buffer(current_pos,length_local))
    current_pos = current_pos + length_local
    number_of_lvs = number_of_lvs - 1
    current_substree_number = current_substree_number + 1
  end
  local payload_len = buffer(current_pos,1):le_uint()
  subtree:add_le(payload_length,payload_len)
  current_pos = current_pos + 1
  subtree:add_le(payload_content, buffer(current_pos, payload_len))
  -- if type_name == "PUBLISHER" then
  -- 	local cache1 = buffer(1,1):le_uint()
  --   local cache_str = get_cache_status(cache1)
  -- 	subtree:add_le(cache_type, buffer(1,1):le_uint()):append_text(" (" .. cache_str .. ")")
  --   local is_forward_val = buffer(2,1):le_uint()
  --   local is_forwarded = get_forward_status(is_forward_val)
  --   subtree:add_le(forwarded, buffer(2,1):le_uint()):append_text(" (" .. is_forwarded .. ")")
  --   local t_len = buffer(3,1):le_uint()
  --   subtree:add_le(topic_length, buffer(3,1):le_uint())
  --   subtree:add_le(topic_payload, buffer(4,t_len))
  --   local sub_len = buffer(4+t_len,1):le_uint()
  --   subtree:add_le(subtopic_length, buffer(4+t_len,1))
  --   subtree:add_le(subtopic_payload, buffer(5+t_len,sub_len))
  --   local p_len = buffer(5+t_len+sub_len,2):uint()
  --   subtree:add_le(payload_length, buffer(5+t_len+sub_len,2):uint())
  --   subtree:add_le(payload_payload, buffer(7+t_len+sub_len,p_len))
  -- elseif type_name == "SUBSCRIBER" then
  --   local miss_var = buffer(1,1):le_uint()
  --   local miss_str = send_missed_packs(miss_var)
  --   subtree:add_le(send_missed, buffer(1,1):le_uint()):append_text(" (" .. miss_str .. ")")
  -- 	local forward_var = buffer(2,1):le_uint()
  --   local forward_str = get_forward_status(forward_var)
  --   subtree:add_le(forwarded, buffer(2,1):le_uint()):append_text(" (" .. forward_str .. ")")
  --   local t_len = buffer(3,1):le_uint()
  --   subtree:add_le(topic_length, buffer(3,1):le_uint())
  --   subtree:add_le(topic_payload, buffer(4,t_len))
  --   local sub_len = buffer(4+t_len,1):le_uint()
  --   subtree:add_le(subtopic_length, buffer(4+t_len,1))
  --   subtree:add_le(subtopic_payload, buffer(5+t_len,sub_len))
  --   local p_len = buffer(5+t_len+sub_len,2):uint()
  --   subtree:add_le(payload_length, buffer(5+t_len+sub_len,2):uint())
  --   subtree:add_le(subscriber_port, buffer(7+t_len+sub_len,p_len):uint())
  -- elseif type_name == "BROKER" then
  --   local from_var = buffer(1,1):le_uint()
  --   local from_str = get_from_status(from_var)
  --   subtree:add_le(from, buffer(1,1):uint()):append_text(" (" .. from_str .. ")")
  --   local status_var = buffer(2,1):uint()
  --   local status_str = has_topic(status_var)
  --   subtree:add_le(status, buffer(2,1):uint()):append_text(" (" .. status_str .. ")")
  --   local t_len = buffer(3,1):le_uint()
  --   subtree:add_le(topic_length, buffer(3,1):le_uint())
  --   subtree:add_le(topic_payload, buffer(4,t_len))
  -- elseif type_name == "ACK" then
  --   local from_var = buffer(1,1):le_uint()
  --   local from_str = get_from_status(from_var)
  --   subtree:add_le(from, buffer(1,1):uint()):append_text(" (" .. from_str .. ")")
  --   local t_len = buffer(2,1):le_uint()
  --   subtree:add_le(topic_length, buffer(2,1):le_uint())
  --   subtree:add_le(topic_payload, buffer(3,t_len))
end

function all_ports(buffer, pinfo, tree)

  forwarding_protocol.dissector(buffer, pinfo, tree)
  return true
end

forwarding_protocol:register_heuristic("udp", all_ports)