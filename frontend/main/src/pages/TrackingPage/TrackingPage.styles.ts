import styled from 'styled-components'

export { Page, Form, Input, Button, TableWrapper, Actions, Table } from '../../components/ui'

export const ClickableId = styled.span`
  cursor: pointer;
  color: #0066cc;
  text-decoration: underline;

  &:hover {
    color: #004499;
  }
`

export const TrackingStrip = styled.div<{ $selected?: boolean }>`
  display: flex;
  align-items: center;
  gap: 12px;
  height: 50px;
  width: 100%;
  padding: 0 20px;
  border-radius: 25px;
  background: ${({ $selected }) => ($selected ? '#dbeafe' : '#f1f5f9')};
  border: 2px solid ${({ $selected }) => ($selected ? '#3b82f6' : 'transparent')};
  cursor: default;
  transition: background 0.15s, border-color 0.15s;

  &:hover {
    background: ${({ $selected }) => ($selected ? '#bfdbfe' : '#e2e8f0')};
  }
`

export const StripId = styled.span`
  cursor: pointer;
  color: #0066cc;
  font-weight: 600;
  font-size: 0.85rem;
  text-decoration: underline;
  white-space: nowrap;

  &:hover {
    color: #004499;
  }
`

export const StripTitle = styled.span`
  font-size: 0.9rem;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
`

export const StripList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`

export const Section = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
`

export const SectionTitle = styled.h2`
  margin: 0;
  font-size: 1rem;
`
